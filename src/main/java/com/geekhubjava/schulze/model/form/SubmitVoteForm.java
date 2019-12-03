package com.geekhubjava.schulze.model.form;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class SubmitVoteForm {

    @NotNull
    @NotEmpty
    private String voteId;

    @NotNull
    private boolean leftBetterThanRight;

    public String getVoteId() {
        return voteId;
    }

    public void setVoteId(String voteId) {
        this.voteId = voteId;
    }

    public boolean isLeftBetterThanRight() {
        return leftBetterThanRight;
    }

    public void setLeftBetterThanRight(boolean leftBetterThanRight) {
        this.leftBetterThanRight = leftBetterThanRight;
    }
}
