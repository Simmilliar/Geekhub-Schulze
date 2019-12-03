package com.geekhubjava.schulze.model;

public class Election {

    private long id;
    private long authorId;
    private String title;
    private String description;
    private boolean closed;
    private String shareId;

    public Election(long id, long authorId, String title, String description, boolean closed, String shareId) {
        this.id = id;
        this.authorId = authorId;
        this.title = title;
        this.description = description;
        this.closed = closed;
        this.shareId = shareId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(long authorId) {
        this.authorId = authorId;
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

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }
}
