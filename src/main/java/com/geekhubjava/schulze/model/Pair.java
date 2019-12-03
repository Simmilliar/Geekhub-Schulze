package com.geekhubjava.schulze.model;

public class Pair {

    private long id;
    private long electionId;
    private Candidate leftCandidate;
    private Candidate rightCandidate;

    public Pair(long id, long electionId, Candidate leftCandidate, Candidate rightCandidate) {
        this.id = id;
        this.electionId = electionId;
        this.leftCandidate = leftCandidate;
        this.rightCandidate = rightCandidate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getElectionId() {
        return electionId;
    }

    public void setElectionId(long electionId) {
        this.electionId = electionId;
    }

    public Candidate getLeftCandidate() {
        return leftCandidate;
    }

    public void setLeftCandidate(Candidate leftCandidate) {
        this.leftCandidate = leftCandidate;
    }

    public Candidate getRightCandidate() {
        return rightCandidate;
    }

    public void setRightCandidate(Candidate rightCandidate) {
        this.rightCandidate = rightCandidate;
    }
}
