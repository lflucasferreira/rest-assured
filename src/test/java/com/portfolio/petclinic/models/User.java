package com.portfolio.petclinic.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    private Integer id;
    private String username;
    private String password;
    private boolean enabled;
    private List<RoleRef> roles = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<RoleRef> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleRef> roles) {
        this.roles = roles;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RoleRef {
        private String name;

        public RoleRef() {
        }

        public RoleRef(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
