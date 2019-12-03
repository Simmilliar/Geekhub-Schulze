package com.geekhubjava.schulze.controller;

import com.geekhubjava.schulze.model.Election;
import com.geekhubjava.schulze.model.UserAuthDetails;
import com.geekhubjava.schulze.model.Vote;
import com.geekhubjava.schulze.model.form.SubmitVoteForm;
import com.geekhubjava.schulze.model.response.VoteToSubmit;
import com.geekhubjava.schulze.repository.ElectionRepository;
import com.geekhubjava.schulze.service.VotingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
public class VotingController {

    private VotingService votingService;
    private ElectionRepository electionRepository;

    @Autowired
    public VotingController(VotingService votingService, ElectionRepository electionRepository) {
        this.votingService = votingService;
        this.electionRepository = electionRepository;
    }

    @GetMapping("/api/elections/{shareId}/vote")
    public VoteToSubmit getPair(@PathVariable String shareId, Authentication authentication) {
        long userId = ((UserAuthDetails) authentication.getPrincipal()).getId();
        Election electionByShareId = electionRepository.getElectionByShareId(shareId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No election with id " + shareId + " were found."));
        Vote voteForUser = votingService.getVoteForUser(electionByShareId.getId(), userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT,
                        "No more pairs available. Thank you for participation!"));
        return new VoteToSubmit(voteForUser);
    }

    @PostMapping("/api/elections/{shareId}/vote")
    @ResponseStatus(HttpStatus.OK)
    public void submitVote(@RequestBody @Valid SubmitVoteForm submitVoteForm, @PathVariable String shareId,
                           Authentication authentication) {
        long userId = ((UserAuthDetails) authentication.getPrincipal()).getId();
        Election election = electionRepository.getElectionByShareId(shareId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No election with id " + shareId + " were found."));
        if (election.isClosed()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Election have been closed.");
        } else if (votingService.canUserSubmitVote(submitVoteForm.getVoteId(), userId)) {
            votingService.submitVote(submitVoteForm, userId);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You can't submit this vote. Consider to reload this page and try again.");
        }
    }
}
