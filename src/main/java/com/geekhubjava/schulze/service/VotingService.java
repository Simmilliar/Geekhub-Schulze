package com.geekhubjava.schulze.service;

import com.geekhubjava.schulze.model.Pair;
import com.geekhubjava.schulze.model.form.SubmitVoteForm;
import com.geekhubjava.schulze.model.Vote;
import com.geekhubjava.schulze.model.VoteResult;
import com.geekhubjava.schulze.repository.VotingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class VotingService {

    private final VotingRepository votingRepository;
    private final SecureRandomStringService secureRandomStringService;

    @Autowired
    public VotingService(VotingRepository votingRepository, SecureRandomStringService secureRandomStringService) {
        this.votingRepository = votingRepository;
        this.secureRandomStringService = secureRandomStringService;
    }

    public Optional<Vote> getVoteForUser(long electionId, long userId) {
        Optional<Vote> notSubmittedVote = votingRepository.getNotSubmittedVoteForUser(electionId, userId);
        if (notSubmittedVote.isPresent()) {
            return notSubmittedVote;
        } else {
            Optional<Pair> newPairForUser = votingRepository.getNewPairForUser(electionId, userId);
            if (newPairForUser.isPresent()) {
                String voteId = secureRandomStringService.getString();
                while (votingRepository.isVoteExists(voteId)) {
                    voteId = secureRandomStringService.getString();
                }
                Vote vote = new Vote(voteId, userId,
                        newPairForUser.get(), VoteResult.NOT_SUBMITTED);
                votingRepository.initializeNewVote(vote);
                return Optional.of(vote);
            } else {
                return Optional.empty();
            }
        }
    }

    @Transactional
    public void submitVote(SubmitVoteForm submitVoteForm, long userId) {
            VoteResult voteResult;
            if (submitVoteForm.isLeftBetterThanRight()) {
                voteResult = VoteResult.LEFT_IS_BETTER;
            } else {
                voteResult = VoteResult.RIGHT_IS_BETTER;
            }
            votingRepository.setVoteResult(submitVoteForm.getVoteId(), voteResult);
            Vote vote = votingRepository.getVoteById(submitVoteForm.getVoteId())
                    .orElseThrow(RuntimeException::new);
            long betterCandidateId;
            long worseCandidateId;
            if (vote.getVoteResult() == VoteResult.LEFT_IS_BETTER) {
                betterCandidateId = vote.getPair().getLeftCandidate().getId();
                worseCandidateId = vote.getPair().getRightCandidate().getId();
            } else {
                betterCandidateId = vote.getPair().getRightCandidate().getId();
                worseCandidateId = vote.getPair().getLeftCandidate().getId();
            }
            List<Vote> missingVotesForCase = votingRepository.getMissingVotesForCase(
                    betterCandidateId, worseCandidateId, userId);
            Set<String> uniqueVoteIds = Stream
                    .generate(secureRandomStringService::getString)
                    .limit(missingVotesForCase.size())
                    .collect(Collectors.toSet());
            while (uniqueVoteIds.size() != missingVotesForCase.size()) {
                uniqueVoteIds.add(secureRandomStringService.getString());
            }
            List<String> uniqueVoteIdsList = new ArrayList<>(uniqueVoteIds);
            for (int i = 0; i < missingVotesForCase.size(); i++) {
                missingVotesForCase.get(i).setId(uniqueVoteIdsList.get(i));
            }
            votingRepository.batchInsertVotes(missingVotesForCase);
    }

    public boolean canUserSubmitVote(String voteId, long userId) {
        return votingRepository.canUserSubmitVote(voteId, userId);
    }
}
