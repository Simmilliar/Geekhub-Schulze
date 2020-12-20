package com.geekhubjava.schulze.model.form;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class NewElectionForm {

    @NotNull
    @NotEmpty
    private String title;

    @NotNull
    private String description;

    @NotNull
    @Size(min = 2, max = 50)
    private List<NewElectionFormCandidate> candidates;

    public NewElectionForm(@NotNull @NotEmpty String title, @NotNull String description,
                           @NotNull @Size(min = 2, max = 50) List<NewElectionFormCandidate> candidates) {
        this.title = title;
        this.description = description;
        this.candidates = candidates;
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

    public List<NewElectionFormCandidate> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<NewElectionFormCandidate> candidates) {
        this.candidates = candidates;
    }
}
