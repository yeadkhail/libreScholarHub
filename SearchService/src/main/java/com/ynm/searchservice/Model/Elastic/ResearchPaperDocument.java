package com.ynm.searchservice.Model.Elastic; // New package recommended

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date; // Use java.util.Date for ES
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "research_papers") // This is the name of the index in Elasticsearch
public class ResearchPaperDocument {

    @Id
    private Integer id; // Use the same ID as your JPA ResearchPaper

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text, analyzer = "english")
    private String abstractText;

    // --- Denormalized Fields ---
    // We will flatten the related data into simple lists for searching

    @Field(type = FieldType.Text)
    private List<String> authors; // Stores the *names* of the authors

    @Field(type = FieldType.Keyword) // Keywords are better for exact matching of tags
    private List<String> tags; // Stores the *names* of the tags

    // --- Other Fields ---

    @Field(type = FieldType.Date)
    private Date createdAt; // Note: Converted from java.sql.Date

    @Field(type = FieldType.Float)
    private Float metric;

    @Field(type = FieldType.Keyword)
    private String visibility;
}