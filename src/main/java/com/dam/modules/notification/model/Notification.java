package com.dam.modules.notification.model;

import com.dam.commons.utils.BaseDateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.dam.modules.user.model.Users;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    Users users;

    private String message;

    private String route;

    private String actionRoute;
    private String actionTitle;
    private int hasAction=0;
    private int isRead;

    private int readOnly;
    private int priority;

    private int damId;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    @JsonIgnore
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Transient
    private String dateTime;

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public int getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(int readOnly) {
        this.readOnly = readOnly;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getDamId() {
        return damId;
    }

    public void setDamId(int damId) {
        this.damId = damId;
    }

    public String getActionRoute() {
        return actionRoute;
    }

    public void setActionRoute(String actionRoute) {
        this.actionRoute = actionRoute;
    }

    public int getHasAction() {
        return hasAction;
    }

    public void setHasAction(int hasAction) {
        this.hasAction = hasAction;
    }

    public String getActionTitle() {
        return actionTitle;
    }

    public void setActionTitle(String actionTitle) {
        this.actionTitle = actionTitle;
    }

    public String getDateTime() {
        return BaseDateUtils.getJalaliDate(getCreatedAt().toLocalDate().toString()).replace("/","-")+" " + getCreatedAt().toLocalTime().toString();
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
