package com.ynm.researchpaperservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ynm.researchpaperservice.Model.Author;
import com.ynm.researchpaperservice.Model.Review;
import com.ynm.researchpaperservice.Model.ResearchPaper;
import com.ynm.researchpaperservice.Repository.ReviewRepository;
import com.ynm.researchpaperservice.Repository.ResearchPaperRepository;
import com.ynm.researchpaperservice.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ResearchPaperRepository researchPaperRepository;
    private final RestTemplate restTemplate;
    private final String searchServiceUrl;
    private final UserDetailsServiceImpl userDetailsService;
    private final JWTService jwtService;
    private final AuthorService authorService;


    public ReviewService(ReviewRepository reviewRepository,
                         ResearchPaperRepository researchPaperRepository,
                         RestTemplate restTemplate,
                         UserDetailsServiceImpl userDetailsService,
                         JWTService jwtService, // Inject your JWT service
                         @Value("${search.service.url}") String searchServiceUrl,
                         AuthorService authorService) {
        this.reviewRepository = reviewRepository;
        this.researchPaperRepository = researchPaperRepository;
        this.restTemplate = restTemplate;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService; // Initialize it
        this.searchServiceUrl = searchServiceUrl;
        this.authorService = authorService;
    }

    // CREATE
    public Review createReview(Review review, Integer paperId) {
        ResearchPaper paper = researchPaperRepository.findById(paperId)
                .orElseThrow(() -> new RuntimeException("Research paper with id " + paperId + " not found."));

        UserScoreService userScoreService = new UserScoreService(restTemplate);

        String userName = "";
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String jwt = authHeader.substring(7);
                userName = jwtService.extractUserName(jwt);
            }
        }

        UserDto user = (UserDto) userDetailsService.loadUserByUsername(userName);
        //          Loads useerId of the reviewer not the authors of the paper
        Long userId = userScoreService.getUserIdByEmail(user.getUsername());
//        System.out.println(userId);


        // 🔍 Check if user already reviewed this paper
        return reviewRepository.findByUserIdAndPaper(userId, paper)
                .map(existingReview -> {
                    // If exists, update instead of creating new
                    review.setId(existingReview.getId());
                    review.setPaper(existingReview.getPaper());
                    review.setTimestamp(new Date());
                    System.out.println("Review already exists with id " + existingReview.getId()+". redirecting to update");
                    return updateReview(existingReview.getId(), review);
                })
                .orElseGet(() -> {
                    List<Author> authors =  authorService.getAuthorsByPaper(paperId);
                    // If not reviewed, create a new one
                    String userRole = user.getAuthorities().stream()
                            .map(Object::toString)
                            .filter(role -> role.equals("ROLE_UNIPUBLISHER") || role.equals("ROLE_ADMIN") || role.equals("ROLE_USER"))
                            .findFirst()
                            .orElse("ROLE_USER");
                    System.out.println("Creating new review for user " + user.getUsername() + userRole);
                    if(userRole.equals("ROLE_UNIPUBLISHER")){
                        float publisherScore = userScoreService.getUserScoreByEmail(user.getUsername());
                        float updateScore = (publisherScore*review.getScore()/10)/10000;
                        review.setLastUpdate(updateScore);
                        for (Author author : authors) {
                            Long authorUserId = author.getUserId(); // make sure Author entity has userId
                            if (authorUserId != null) {
                                userScoreService.syncScore(authorUserId, updateScore, 0f);
                            }
                        }
                    }
                    else if(userRole.equals("ROLE_USER")){
                        float userScore = userScoreService.getUserScoreByEmail(user.getUsername());
                        float updateScore = (userScore*review.getScore()/10)/10000;
                        review.setLastUpdate(updateScore);
                        for (Author author : authors) {
                            Long authorUserId = author.getUserId(); // make sure Author entity has userId
                            if (authorUserId != null) {
                                userScoreService.syncScore(authorUserId, updateScore, 0f);
                            }
                        }
                    }

                    review.setPaper(paper);
                    review.setTimestamp(new Date());
                    review.setUserId(userId);
                    Review savedReview = reviewRepository.save(review);

                    // Sync to SearchService
                    syncReviewToSearchService(savedReview);
                    return savedReview;
                });
    }


    // UPDATE
    public Review updateReview(Integer id, Review updatedReview) {
        Review existing = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        UserScoreService userScoreService = new UserScoreService(restTemplate);

        String userName = "";
        // Extract JWT token and username
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String jwt = authHeader.substring(7);
                userName = jwtService.extractUserName(jwt);

            }
        }
//        System.out.println("Updating review with username " + userName);
        UserDto user = (UserDto) userDetailsService.loadUserByUsername(userName);
        String userRole = user.getAuthorities().stream()
                .map(Object::toString)
                .filter(role -> role.equals("ROLE_UNIPUBLISHER") || role.equals("ROLE_ADMIN") || role.equals("ROLE_USER"))
                .findFirst()
                .orElse("ROLE_USER");
//        System.out.println("paperId " + (updatedReview));

        List<Author> authors =  authorService.getAuthorsByPaper(updatedReview.getPaper().getId());

        if(userRole.equals("ROLE_UNIPUBLISHER")){

            float previousvalue = existing.getLastUpdate();
            float publisherScore = userScoreService.getUserScoreByEmail(user.getUsername());//paper.getPublisher().getPublisherScore();
            float updateScore = (publisherScore*updatedReview.getScore()/10)/10000;
            updatedReview.setLastUpdate(updateScore);
            ResearchPaper paper = researchPaperRepository.findById(existing.getPaper().getId())
                    .orElseThrow(() -> new RuntimeException("Research paper with id " + existing.getPaper().getId() + " not found."));
            paper.addMetric(updateScore);
            for (Author author : authors) {
                Long authorUserId = author.getUserId(); // make sure Author entity has userId
                if (authorUserId != null) {
                    userScoreService.syncScore(authorUserId, updateScore, previousvalue);
                }
            }

        }
        else if(userRole.equals("ROLE_USER")){
            float previousvalue = existing.getLastUpdate();
            float userScore = userScoreService.getUserScoreByEmail(user.getUsername());
            float updateScore = (userScore*updatedReview.getScore()/10)/10000;
            updatedReview.setLastUpdate(updateScore);
            ResearchPaper paper = researchPaperRepository.findById(existing.getPaper().getId())
                    .orElseThrow(() -> new RuntimeException("Research paper with id " + existing.getPaper().getId() + " not found."));
            paper.addMetric(updateScore);
            for (Author author : authors) {
                Long authorUserId = author.getUserId(); // make sure Author entity has userId
                if (authorUserId != null) {
                    userScoreService.syncScore(authorUserId, updateScore, previousvalue);
                }
            }
            // to do : send the update to the user service"
        }

        if(updatedReview.getScore() != null) existing.setScore(updatedReview.getScore());
        if(updatedReview.getComment() != null) existing.setComment(updatedReview.getComment());
        if(updatedReview.getTimestamp() != null) existing.setTimestamp(new Date());

        if (updatedReview.getPaper() != null) {
            Integer paperId = updatedReview.getPaper().getId();
            ResearchPaper paper = researchPaperRepository.findById(paperId)
                    .orElseThrow(() -> new RuntimeException("Research paper not found"));
            existing.setPaper(paper);
        }

        Review saved = reviewRepository.save(existing);

        syncReviewToSearchService(saved);
//        System.out.println("Review updated successfully");
        return saved;
    }

    // DELETE
    public Review deleteReview(Integer id) {
        Review existing = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review with id " + id + " not found."));
        reviewRepository.delete(existing);

        // Sync deletion to SearchService
        deleteReviewFromSearchService(id);

        return existing;
    }

    // GET by ID
    public Review getReviewById(Integer id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review with id " + id + " not found."));
    }

    // GET all
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    // ---------------------- Helper methods for SearchService ----------------------

    private void syncReviewToSearchService(Review review) {
        try {
            String url = searchServiceUrl + "/reviews/sync";
            log.debug("Syncing review to Search Service at: {}", url);

            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            if (attrs != null) {
                String bearerToken = attrs.getRequest().getHeader("Authorization");
                if (bearerToken != null && !bearerToken.isEmpty()) {
                    headers.set("Authorization", bearerToken);
                }
            }

            HttpEntity<Review> entity = new HttpEntity<>(review, headers);
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);

            log.debug("Review sync response: {}", response.getStatusCode());
        } catch (Exception e) {
            log.error("Failed to sync review with Search Service: {}", e.getMessage(), e);
        }
    }

    private void deleteReviewFromSearchService(Integer reviewId) {
        try {
            String url = searchServiceUrl + "/reviews/sync/" + reviewId;
            log.debug("Deleting review in Search Service at: {}", url);

            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            if (attrs != null) {
                String bearerToken = attrs.getRequest().getHeader("Authorization");
                if (bearerToken != null && !bearerToken.isEmpty()) {
                    headers.set("Authorization", bearerToken);
                }
            }

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);

            log.debug("Review delete sync response: {}", response.getStatusCode());
        } catch (Exception e) {
            log.error("Failed to delete review in Search Service: {}", e.getMessage(), e);
        }
    }
}
