package com.geekhubjava.schulze.service;

import com.geekhubjava.schulze.model.Candidate;
import com.geekhubjava.schulze.model.VotingSummaryData;
import com.geekhubjava.schulze.repository.VotingRepository;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class XLSExportingService {

    private VotingRepository votingRepository;
    private CalculationService calculationService;

    @Autowired
    public XLSExportingService(VotingRepository votingRepository, CalculationService calculationService) {
        this.votingRepository = votingRepository;
        this.calculationService = calculationService;
    }

    public HSSFWorkbook generateXLSFileForElection(long electionId) {
        List<VotingSummaryData> votingSummaryPerPairForElection =
                votingRepository.getVotingSummaryPerPairForElection(electionId);
        List<Candidate> sortedCandidates = calculationService.getSortedCandidates(electionId);

        HSSFWorkbook workbook = new HSSFWorkbook();

        HSSFSheet votesSheet = workbook.createSheet("Votes");
        HSSFRow votesHeader = votesSheet.createRow(0);
        votesHeader.createCell(0).setCellValue("Candidate A");
        votesHeader.createCell(1).setCellValue("Candidate B");
        votesHeader.createCell(2).setCellValue("Votes for A");
        votesHeader.createCell(3).setCellValue("Votes for B");
        int rowNum = 1;
        for (VotingSummaryData votingSummaryData : votingSummaryPerPairForElection) {
            HSSFRow row = votesSheet.createRow(rowNum);
            row.createCell(0).setCellValue(votingSummaryData.getLeftCandidateName());
            row.createCell(1).setCellValue(votingSummaryData.getRightCandidateName());
            row.createCell(2).setCellValue(votingSummaryData.getLeftVotes());
            row.createCell(3).setCellValue(votingSummaryData.getRightVotes());
            rowNum++;
        }

        HSSFSheet ratingSheet = workbook.createSheet("Rating");
        HSSFRow ratingHeader = ratingSheet.createRow(0);
        ratingHeader.createCell(0).setCellValue("Candidate name");
        ratingHeader.createCell(1).setCellValue("Score");
        rowNum = 1;
        for (Candidate candidate : sortedCandidates) {
            HSSFRow row = ratingSheet.createRow(rowNum);
            row.createCell(0).setCellValue(candidate.getName());
            row.createCell(1).setCellValue(candidate.getScore());
            rowNum++;
        }

        return workbook;
    }
}
