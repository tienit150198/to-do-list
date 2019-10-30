package com.example.todolist;

import java.util.Date;

public class Note {
    private int nodeId = -1;
    private String content, subList;
    private Date dateCreate, deadline;
    private boolean isCompleted, isHightlight;
    private int priority;

    public Note() {
    }

    public Note(int nodeId, String content, String subList, Date dateCreate, Date deadline, boolean isCompleted, boolean isHightlight, int priority) {
        this.nodeId = nodeId;
        this.content = content;
        this.subList = subList;
        this.dateCreate = dateCreate;
        this.deadline = deadline;
        this.isCompleted = isCompleted;
        this.isHightlight = isHightlight;
        this.priority = priority;
    }

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSubList() {
        return subList;
    }

    public void setSubList(String subList) {
        this.subList = subList;
    }

    public Date getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(Date dateCreate) {
        this.dateCreate = dateCreate;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public boolean isHightlight() {
        return isHightlight;
    }

    public void setHightlight(boolean hightlight) {
        isHightlight = hightlight;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
