package com.geekhubjava.schulze.model.response;

import com.geekhubjava.schulze.model.Vote;

public class VoteToSubmit {

    private String voteId;
    private CandidateInfo leftCandidate;
    private CandidateInfo rightCandidate;

    public VoteToSubmit(Vote vote) {
        this.voteId = vote.getId();
        this.leftCandidate = new CandidateInfo(vote.getPair().getLeftCandidate());
        this.rightCandidate = new CandidateInfo(vote.getPair().getRightCandidate());
    }

    public String getVoteId() {
        return voteId;
    }

    public void setVoteId(String voteId) {
        this.voteId = voteId;
    }

    public CandidateInfo getLeftCandidate() {
        return leftCandidate;
    }

    public void setLeftCandidate(CandidateInfo leftCandidate) {
        this.leftCandidate = leftCandidate;
    }

    public CandidateInfo getRightCandidate() {
        return rightCandidate;
    }

    public void setRightCandidate(CandidateInfo rightCandidate) {
        this.rightCandidate = rightCandidate;
    }
}
