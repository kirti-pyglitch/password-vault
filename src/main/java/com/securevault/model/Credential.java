package com.securevault.model;

import java.io.Serializable;
import java.util.UUID;

/**
 * Core credential entity representing a secured account record.
 */
public class Credential implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private String url;
    private String username;
    private String password;
    private Category category;
    private String notes;
    private long createdAt;
    private long updatedAt;
    private boolean favorite;

    public Credential() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.category = Category.LOGINS;
    }

    public Credential(String title, String url, String username, String password, Category category, String notes) {
        this();
        this.title = title;
        this.url = url;
        this.username = username;
        this.password = password;
        this.category = category != null ? category : Category.LOGINS;
        this.notes = notes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title != null ? title : "";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url != null ? url : "";
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username != null ? username : "";
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password != null ? password : "";
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Category getCategory() {
        return category != null ? category : Category.LOGINS;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getNotes() {
        return notes != null ? notes : "";
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getInitial() {
        if (title != null && !title.trim().isEmpty()) {
            return title.trim().substring(0, 1).toUpperCase();
        }
        return "S";
    }

    public Credential copy() {
        Credential c = new Credential();
        c.id = this.id;
        c.title = this.title;
        c.url = this.url;
        c.username = this.username;
        c.password = this.password;
        c.category = this.category;
        c.notes = this.notes;
        c.createdAt = this.createdAt;
        c.updatedAt = this.updatedAt;
        c.favorite = this.favorite;
        return c;
    }
}

