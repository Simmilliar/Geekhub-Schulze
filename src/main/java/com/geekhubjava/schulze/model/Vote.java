package com.geekhubjava.schulze.model;

public class Vote {

    private String id;
    private long userId;
    private Pair pair;
    private VoteResult voteResult;

    public Vote(String id, long userId, Pair pair, VoteResult voteResult) {
        this.id = id;
        this.userId = userId;
        this.pair = pair;
        this.voteResult = voteResult;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public VoteResult getVoteResult() {
        return voteResult;
    }

    public void setVoteResult(VoteResult voteResult) {
        this.voteResult = voteResult;
    }

    public Pair getPair() {
        return pair;
    }

    public void setPair(Pair pair) {
        this.pair = pair;
    }

    @Override
    public String toString() {
        return "Vote{" +
                "id='" + id + '\'' +
                ", userId=" + userId +
                ", pair=" + pair +
                ", voteResult=" + voteResult +
                '}';
    }
}
