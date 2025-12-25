package com.example.keshe.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


import java.util.Date;

@Entity(tableName = "tasks")

public class Task {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String description;

    @ColumnInfo(name = "category")
    private String category;

    private int priority; // 1-低，2-中，3-高

    @ColumnInfo(name = "due_date")
    private Date dueDate;

    private int status; // 0-未开始，1-进行中，2-已完成

    @ColumnInfo(name = "is_recurring")
    private boolean isRecurring;

    @ColumnInfo(name = "recurrence_pattern")
    private String recurrencePattern;

    @ColumnInfo(name = "created_at")
    private Date createdAt;

    @ColumnInfo(name = "notification_id")
    private int notificationId;

    // 构造函数
    public Task() {
        this.createdAt = new Date();
        this.notificationId = (int) System.currentTimeMillis();
    }

    // Getter和Setter方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
    }

    public String getRecurrencePattern() {
        return recurrencePattern;
    }

    public void setRecurrencePattern(String recurrencePattern) {
        this.recurrencePattern = recurrencePattern;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }
}