package com.geekhubjava.schulze.model.response;

import com.geekhubjava.schulze.model.Candidate;

public class CandidateInfo {

    private String name;
    private String description;
    private int score;

    public CandidateInfo(Candidate candidate) {
        this.name = candidate.getName();
        this.description = candidate.getDescription();
        this.score = candidate.getScore();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
