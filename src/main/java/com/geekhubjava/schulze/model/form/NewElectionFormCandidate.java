package com.geekhubjava.schulze.model.form;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class NewElectionFormCandidate {

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    private String description;

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
}
