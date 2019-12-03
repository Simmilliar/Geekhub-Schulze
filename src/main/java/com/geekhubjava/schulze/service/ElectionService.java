package com.geekhubjava.schulze.service;

import com.geekhubjava.schulze.model.Candidate;
import com.geekhubjava.schulze.model.Election;
import com.geekhubjava.schulze.model.form.NewElectionForm;
import com.geekhubjava.schulze.model.response.Page;
import com.geekhubjava.schulze.repository.CandidatesRepository;
import com.geekhubjava.schulze.repository.ElectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ElectionService {

    private ElectionRepository electionRepository;
    private CandidatesRepository candidatesRepository;
    private SecureRandomStringService secureRandomStringService;

    @Autowired
    public ElectionService(ElectionRepository electionRepository, CandidatesRepository candidatesRepository,
                           SecureRandomStringService secureRandomStringService) {
        this.electionRepository = electionRepository;
        this.candidatesRepository = candidatesRepository;
        this.secureRandomStringService = secureRandomStringService;
    }

    public boolean isTitleAlreadyExists(String title) {
        return electionRepository.isTitleAlreadyExists(title);
    }

    @Transactional
    public Election createNewElection(NewElectionForm newElectionForm, long authorId) {
        String shareId = secureRandomStringService.getString();
        while (getElection(shareId).isPresent()) {
            shareId = secureRandomStringService.getString();
        }
        Election newElection = new Election(0, authorId, newElectionForm.getTitle(), newElectionForm.getDescription(),
                false, shareId);
        List<Candidate> newCandidates = newElectionForm.getCandidates().stream()
                .map(candidateForm -> new Candidate(0, 0, candidateForm.getName(),
                        candidateForm.getDescription()))
                .collect(Collectors.toList());
        electionRepository.insertNewElection(newElection);
        for (Candidate candidate : newCandidates) {
            candidate.setElectionId(newElection.getId());
        }
        candidatesRepository.insertElectionCandidates(newCandidates);
        electionRepository.generatePairsForElection(newElection.getId());
        return newElection;
    }

    public Optional<Election> getElection(String shareId) {
        return electionRepository.getElectionByShareId(shareId);
    }

    public List<Election> getUsersElections(long userId) {
        return electionRepository.getElectionsByAuthorId(userId);
    }

    public Page<Election> getUsersElectionsPaged(long userId, int page, int perPage) {
        return electionRepository.getElectionsByAuthorIdPaged(userId, page, perPage);
    }

    public List<Election> getUsersParticipations(long userId) {
        return electionRepository.getParticipationsByUserId(userId);
    }

    public Page<Election> getUsersParticipationsPaged(long userId, int page, int perPage) {
        return electionRepository.getParticipationsByUserIdPaged(userId, page, perPage);
    }

    public void closeElection(String shareId) {
        electionRepository.closeElection(shareId);
    }
}
