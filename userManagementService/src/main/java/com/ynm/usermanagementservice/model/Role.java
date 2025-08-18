package com.ynm.usermanagementservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "role")
public class Role extends MasterEntity {
    private String name;
}
