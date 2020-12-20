package com.geekhubjava.schulze.service;

import com.geekhubjava.schulze.model.Candidate;
import com.geekhubjava.schulze.model.Pair;
import com.geekhubjava.schulze.model.Vote;
import com.geekhubjava.schulze.model.VoteResult;
import com.geekhubjava.schulze.repository.CandidatesRepository;
import com.geekhubjava.schulze.repository.VotingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CalculationService {

    private final VotingRepository votingRepository;
    private final CandidatesRepository candidatesRepository;

    @Autowired
    public CalculationService(VotingRepository votingRepository, CandidatesRepository candidatesRepository) {
        this.votingRepository = votingRepository;
        this.candidatesRepository = candidatesRepository;
    }

    public List<Candidate> getSortedCandidates(long electionId) {
        List<Candidate> candidates = candidatesRepository.getElectionCandidates(electionId);
        List<Vote> votes = votingRepository.getVotesForElection(electionId);

        Map<Long, Integer> idToIndexMap = new HashMap<>();
        int index = 0;
        for (Candidate candidate : candidates) {
            idToIndexMap.put(candidate.getId(), index);
            index++;
        }

        int[][] d = new int[candidates.size()][candidates.size()];
        for (Vote vote : votes) {
            Pair pair = vote.getPair();
            int rightIndex = idToIndexMap.get(pair.getRightCandidate().getId());
            int leftIndex = idToIndexMap.get(pair.getLeftCandidate().getId());
            if (vote.getVoteResult() == VoteResult.RIGHT_IS_BETTER) {
                d[rightIndex][leftIndex]++;
            } else {
                d[leftIndex][rightIndex]++;
            }
        }

        int[][] p = new int[candidates.size()][candidates.size()];
        for (int i = 0; i < candidates.size(); i++) {
            for (int j = 0; j < candidates.size(); j++) {
                if (i != j) {
                    if (d[i][j] > d[j][i]) {
                        p[i][j] = d[i][j];
                    } else {
                        p[i][j] = 0;
                    }
                }
            }
        }
        for (int i = 0; i < candidates.size(); i++) {
            for (int j = 0; j < candidates.size(); j++) {
                if (i != j) {
                    for (int k = 0; k < candidates.size(); k++) {
                        if (i != k && j != k) {
                            p[j][k] = Math.max(p[j][k], Math.min(p[j][i], p[i][k]));
                        }
                    }
                }
            }
        }

        candidates.sort((c1, c2) -> {
            int c1Index = idToIndexMap.get(c1.getId());
            int c2Index = idToIndexMap.get(c2.getId());
            return p[c1Index][c2Index] - p[c2Index][c1Index];
        });

        Candidate previousCandidate = null;
        for (Candidate candidate : candidates) {
            if (previousCandidate != null) {
                int candidateIndex = idToIndexMap.get(candidate.getId());
                int previousCandidateIndex = idToIndexMap.get(previousCandidate.getId());
                candidate.setScore(previousCandidate.getScore() +
                        p[candidateIndex][previousCandidateIndex] -
                        p[previousCandidateIndex][candidateIndex]);
            } else {
                candidate.setScore(0);
            }
            previousCandidate = candidate;
        }

        Collections.reverse(candidates);

        return candidates;
    }
}
