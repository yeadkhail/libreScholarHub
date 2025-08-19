package com.ynm.usermanagementservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ynm.usermanagementservice.model.MasterEntity;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Entity
@Table(name = "users")
public class User extends MasterEntity implements UserDetails {
    @JsonProperty("userName")
    private String userName;
    private String email;
    private String password;
    private String fullName;
    @Column(name = "otp_verified", nullable = false)
    private boolean otpVerified;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name = "user_photo", nullable = true)
    private String userPhoto;
    @Column(name = "address", nullable = true)
    private String address;
    @Column(name = "latitude", nullable = true)
    private Double latitude;
    @Column(name = "longitude", nullable = true)
    private Double longitude;

    @Transient
    @JsonProperty("authorities")
    private List<String> authorityRoles;

    @PostLoad
    private void initAuthorities() {
        this.authorityRoles = List.of(role.getName());
    }

    @JsonProperty("authorities")
    public void setAuthorityRoles(List<String> roles) {  // Keep only this setter
        this.authorityRoles = roles;
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorityRoles != null
                ? authorityRoles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
                : Collections.emptyList();
    }

    @Override
    @JsonProperty("userName")
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    private boolean doRoleMatch(String roleName) {
        return role.getName().equals(roleName);
    }

    public boolean isAdmin() {
        return doRoleMatch("ROLE_ADMIN");
    }

    public boolean isSuperAdmin() {
        return doRoleMatch("ROLE_SUPER_ADMIN");
    }

    public boolean isUser() {
        return doRoleMatch("ROLE_USER");
    }

    // In 'userManagementService/src/main/java/com/ynm/usermanagementservice/model/User.java'
    private boolean isVerified = false;

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        this.isVerified = verified;
    }
}
