package com.ynm.researchpaperservice.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MeController {

    @GetMapping("/me")
    public Map<String, Object> me(@AuthenticationPrincipal Jwt jwt) {
        Map<String, Object> res = new HashMap<>();
        res.put("sub", jwt.getSubject());
        res.put("email", jwt.getClaim("email")); // will be null unless you add it when issuing tokens
        List<String> roles = jwt.getClaimAsStringList("roles"); // optional
        res.put("roles", roles);
        return res;
    }
}



//---------------to use the names from the security service


//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
//// ...
//JwtAuthenticationToken auth = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
//String userId = auth.getName(); // set to sub if your converter did that
//Object email = auth.getToken().getClaim("email");