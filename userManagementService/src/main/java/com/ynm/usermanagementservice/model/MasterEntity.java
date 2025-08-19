package com.ynm.usermanagementservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@MappedSuperclass
@Data
public class MasterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @Schema(hidden = true)
    @NotNull
    private Boolean isActive = true;

    @JsonIgnore
    @CreationTimestamp
    @Schema(hidden = true)
    @NotNull
    private OffsetDateTime createdOn = OffsetDateTime.now();  // Add default value

    @JsonIgnore
    @UpdateTimestamp
    @Schema(hidden = true)
    @NotNull
    private OffsetDateTime updatedOn = OffsetDateTime.now();  // Add default value
}