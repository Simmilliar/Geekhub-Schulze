package com.geekhubjava.schulze.controller;

import com.geekhubjava.schulze.model.Candidate;
import com.geekhubjava.schulze.model.Election;
import com.geekhubjava.schulze.model.UserAuthDetails;
import com.geekhubjava.schulze.model.form.NewElectionForm;
import com.geekhubjava.schulze.model.form.NewElectionFormCandidate;
import com.geekhubjava.schulze.model.response.ElectionInfo;
import com.geekhubjava.schulze.model.response.Page;
import com.geekhubjava.schulze.service.CalculationService;
import com.geekhubjava.schulze.service.ElectionService;
import com.geekhubjava.schulze.service.XLSExportingService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ElectionsController {

    private ElectionService electionService;
    private CalculationService calculationService;
    private XLSExportingService xlsExportingService;

    @Autowired
    public ElectionsController(ElectionService electionService, CalculationService calculationService,
                               XLSExportingService xlsExportingService) {
        this.electionService = electionService;
        this.calculationService = calculationService;
        this.xlsExportingService = xlsExportingService;
    }

    @GetMapping("/api/elections/{shareId}")
    public ElectionInfo getElectionInfo(@PathVariable String shareId) {
        Election election = electionService.getElection(shareId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No election with id " + shareId + " were found."));
        List<Candidate> candidates = calculationService.getSortedCandidates(election.getId());
        return new ElectionInfo(election, candidates);
    }

    @GetMapping("/api/elections")
    public Page<ElectionInfo> getMyElections(@RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "5") int perPage,
                                             Authentication authentication) {
        long userId = ((UserAuthDetails) authentication.getPrincipal()).getId();
        Page<Election> usersElections = electionService.getUsersElectionsPaged(userId, page, perPage);
        return new Page<>(usersElections.getPage(), usersElections.getPerPage(), usersElections.getItemsTotal(),
                usersElections.getItems().stream().map(ElectionInfo::new).collect(Collectors.toList()));
    }

    @GetMapping("/api/participations")
    public Page<ElectionInfo> getMyParticipations(@RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "5") int perPage,
                                                  Authentication authentication) {
        long userId = ((UserAuthDetails) authentication.getPrincipal()).getId();
        Page<Election> usersElections = electionService.getUsersParticipationsPaged(userId, page, perPage);
        return new Page<>(usersElections.getPage(), usersElections.getPerPage(), usersElections.getItemsTotal(),
                usersElections.getItems().stream().map(ElectionInfo::new).collect(Collectors.toList()));
    }

    @PostMapping("/api/elections")
    public ElectionInfo createElection(@RequestBody @Valid NewElectionForm newElectionForm, Authentication authentication) {
        if (electionService.isTitleAlreadyExists(newElectionForm.getTitle())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The election with such title is already exists.");
        }
        int uniqueCandidates = newElectionForm.getCandidates().stream()
                .map(NewElectionFormCandidate::getName)
                .collect(Collectors.toSet())
                .size();
        if (newElectionForm.getCandidates().size() != uniqueCandidates) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Election cannot contain two or more candidates with same name.");
        }
        long authorId = ((UserAuthDetails) authentication.getPrincipal()).getId();
        return new ElectionInfo(electionService.createNewElection(newElectionForm, authorId));
    }

    @DeleteMapping("/api/elections/{shareId}")
    public void closeElection(@PathVariable String shareId, Authentication authentication) {
        long authorId = ((UserAuthDetails) authentication.getPrincipal()).getId();
        Election election = electionService.getElection(shareId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No election with id " + shareId + " were found."));
        if (election.getAuthorId() != authorId) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        } else {
            electionService.closeElection(shareId);
        }
    }

    @GetMapping("/api/elections/{shareId}/summary")
    public void getElectionSummary(@PathVariable String shareId, Authentication authentication,
                                   HttpServletResponse response) {
        long authorId = ((UserAuthDetails) authentication.getPrincipal()).getId();
        Election election = electionService.getElection(shareId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No election with id " + shareId + " were found."));
        if (election.getAuthorId() != authorId) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        } else if (!election.isClosed()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You can obtain election summary only after closing it.");
        } else {
            HSSFWorkbook workbook = xlsExportingService.generateXLSFileForElection(election.getId());
            try {
                response.setContentType("application/excel");
                response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + shareId + "_summary.xls");
                workbook.write(response.getOutputStream());
                response.flushBuffer();
            } catch (IOException e) {
                throw new RuntimeException("Error during XLS file export.", e);
            } finally {
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
