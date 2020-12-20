import com.geekhubjava.schulze.model.Candidate;
import com.geekhubjava.schulze.model.Pair;
import com.geekhubjava.schulze.model.Vote;
import com.geekhubjava.schulze.model.VoteResult;
import com.geekhubjava.schulze.repository.CandidatesRepository;
import com.geekhubjava.schulze.repository.VotingRepository;
import com.geekhubjava.schulze.service.CalculationService;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

public class CalculationServiceTest {

    @Test
    public void getSortedCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            candidates.add(new Candidate(i, 0, "" + (char)((int)'A' + i), ""));
        }
        String[] ballots = new String[]{
                "ACBED", "ACBED", "ACBED", "ACBED", "ACBED",
                "ADECB", "ADECB", "ADECB", "ADECB", "ADECB",
                "BEDAC", "BEDAC", "BEDAC", "BEDAC", "BEDAC", "BEDAC", "BEDAC", "BEDAC",
                "CABED", "CABED", "CABED",
                "CAEBD", "CAEBD", "CAEBD", "CAEBD", "CAEBD", "CAEBD", "CAEBD",
                "CBADE", "CBADE",
                "DCEBA", "DCEBA", "DCEBA", "DCEBA", "DCEBA", "DCEBA", "DCEBA",
                "EBADC", "EBADC", "EBADC", "EBADC", "EBADC", "EBADC", "EBADC", "EBADC"
        };
        List<Vote> votes = new ArrayList<>();
        for (int userId = 0; userId < ballots.length; userId++) {
            String ballot = ballots[userId];
            for (int i = 0; i < ballot.length(); i++) {
                for (int j = i + 1; j < ballot.length(); j++) {
                    char a = ballot.charAt(i);
                    char b = ballot.charAt(j);
                    Candidate leftCandidate = candidates.get(Math.min((int)a - (int)'A', (int)b - (int)'A'));
                    Candidate rightCandidate = candidates.get(Math.max((int)a - (int)'A', (int)b - (int)'A'));
                    String voteId = "L" + leftCandidate.getId() + "R" + rightCandidate.getId() + "U" + userId;
                    long pairId = leftCandidate.getId() + 5 * rightCandidate.getId();
                    VoteResult voteResult = (int)a == ((int)'A' + leftCandidate.getId()) ? VoteResult.LEFT_IS_BETTER : VoteResult.RIGHT_IS_BETTER;
                    votes.add(new Vote(voteId, userId, new Pair(pairId, 0, leftCandidate, rightCandidate), voteResult));
                }
            }
        }

        VotingRepository votingRepository = Mockito.mock(VotingRepository.class);
        Mockito.when(votingRepository.getVotesForElection(Mockito.anyLong())).thenReturn(votes);
        CandidatesRepository candidatesRepository = Mockito.mock(CandidatesRepository.class);
        Mockito.when(candidatesRepository.getElectionCandidates(Mockito.anyLong())).thenReturn(candidates);

        CalculationService calculationService = new CalculationService(votingRepository, candidatesRepository);
        List<Candidate> sortedCandidates = calculationService.getSortedCandidates(0);
        Assert.assertArrayEquals(new String[]{ "E", "A", "C", "B", "D" },
                sortedCandidates.stream().map(Candidate::getName).toArray());
    }
}
