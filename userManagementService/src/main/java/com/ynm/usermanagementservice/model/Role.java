package com.ynm.usermanagementservice.model;

import com.ynm.usermanagementservice.model.MasterEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Data
@Getter
@Setter
@Entity
@Table(name = "role")
public class Role extends MasterEntity {
    private String name;
}