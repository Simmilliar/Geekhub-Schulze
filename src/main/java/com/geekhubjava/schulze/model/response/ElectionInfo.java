package com.geekhubjava.schulze.model.response;

import com.geekhubjava.schulze.model.Candidate;
import com.geekhubjava.schulze.model.Election;
import com.geekhubjava.schulze.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class ElectionInfo {

    private String author;
    private String shareId;
    private String title;
    private String description;
    private boolean closed;
    private List<CandidateInfo> candidates;

    public ElectionInfo(Election election) {
        this.shareId = election.getShareId();
        this.title = election.getTitle();
        this.description = election.getDescription();
        this.closed = election.isClosed();
        this.candidates = null;
    }

    public ElectionInfo(Election election, User author, List<Candidate> candidates) {
        this.author = author.getLogin();
        this.shareId = election.getShareId();
        this.title = election.getTitle();
        this.description = election.getDescription();
        this.closed = election.isClosed();
        this.candidates = candidates.stream()
                .map(CandidateInfo::new)
                .collect(Collectors.toList());
    }

    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
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

    public List<CandidateInfo> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<CandidateInfo> candidates) {
        this.candidates = candidates;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
