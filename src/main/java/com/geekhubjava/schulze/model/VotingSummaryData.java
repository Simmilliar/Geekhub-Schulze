package com.geekhubjava.schulze.model;

public class VotingSummaryData {

    private String leftCandidateName;
    private String rightCandidateName;
    private int leftVotes;
    private int rightVotes;

    public VotingSummaryData(String leftCandidateName, String rightCandidateName, int leftVotes, int rightVotes) {
        this.leftCandidateName = leftCandidateName;
        this.rightCandidateName = rightCandidateName;
        this.leftVotes = leftVotes;
        this.rightVotes = rightVotes;
    }

    public String getLeftCandidateName() {
        return leftCandidateName;
    }

    public void setLeftCandidateName(String leftCandidateName) {
        this.leftCandidateName = leftCandidateName;
    }

    public String getRightCandidateName() {
        return rightCandidateName;
    }

    public void setRightCandidateName(String rightCandidateName) {
        this.rightCandidateName = rightCandidateName;
    }

    public int getLeftVotes() {
        return leftVotes;
    }

    public void setLeftVotes(int leftVotes) {
        this.leftVotes = leftVotes;
    }

    public int getRightVotes() {
        return rightVotes;
    }

    public void setRightVotes(int rightVotes) {
        this.rightVotes = rightVotes;
    }
}
