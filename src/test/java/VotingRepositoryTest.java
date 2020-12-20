import com.geekhubjava.schulze.Application;
import com.geekhubjava.schulze.model.*;
import com.geekhubjava.schulze.model.form.NewElectionForm;
import com.geekhubjava.schulze.model.form.NewElectionFormCandidate;
import com.geekhubjava.schulze.model.form.RegistrationForm;
import com.geekhubjava.schulze.model.form.SubmitVoteForm;
import com.geekhubjava.schulze.repository.VotingRepository;
import com.geekhubjava.schulze.service.ElectionService;
import com.geekhubjava.schulze.service.UserService;
import com.geekhubjava.schulze.service.VotingService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class VotingRepositoryTest {

    @Autowired
    private UserService userService;
    @Autowired
    private ElectionService electionService;
    @Autowired
    private VotingRepository votingRepository;
    @Autowired
    private VotingService votingService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Before
    public void cleanDatabase() {
        jdbcTemplate.execute("TRUNCATE votes, pairs, candidates, elections, users");
    }

    @Test
    public void getMissingVotesForCase() {
        User user = userService.registerUser(new RegistrationForm("alice", "alice"));
        List<NewElectionFormCandidate> newElectionFormCandidates = new ArrayList<>();
        newElectionFormCandidates.add(new NewElectionFormCandidate("A", "A"));
        newElectionFormCandidates.add(new NewElectionFormCandidate("B", "B"));
        newElectionFormCandidates.add(new NewElectionFormCandidate("C", "C"));
        newElectionFormCandidates.add(new NewElectionFormCandidate("D", "D"));
        newElectionFormCandidates.add(new NewElectionFormCandidate("E", "E"));
        NewElectionForm newElectionForm = new NewElectionForm("Test election",
                "Test election description", newElectionFormCandidates);
        electionService.createNewElection(newElectionForm, user.getId());

        RowMapper<Pair> pairRowMapper = (rs, rowNum) -> new Pair(
                rs.getLong("p_id"),
                rs.getLong("p_election"),
                new Candidate(
                        rs.getLong("cl_id"),
                        rs.getLong("p_election"),
                        rs.getString("cl_name"),
                        rs.getString("cl_description")
                ),
                new Candidate(
                        rs.getLong("cr_id"),
                        rs.getLong("p_election"),
                        rs.getString("cr_name"),
                        rs.getString("cr_description")
                )
        );
        String pairsQuery =
                "SELECT p.id AS p_id, p.election AS p_election, " +
                "   cl.id AS cl_id, cl.name AS cl_name, cl.description AS cl_description, " +
                "   cr.id AS cr_id, cr.name AS cr_name, cr.description AS cr_description " +
                "FROM pairs p LEFT JOIN votes v ON v.pair = p.id " +
                "   JOIN candidates cl ON cl.id = p.left_candidate " +
                "   JOIN candidates cr ON cr.id = p.right_candidate";
        List<Pair> pairs = jdbcTemplate.query(pairsQuery, pairRowMapper);
        for (Pair pair : pairs) {
            if (pair.getRightCandidate().getName().compareTo("D") < 0 && pair.getLeftCandidate().getName().compareTo("D") < 0 ||
                    pair.getRightCandidate().getName().compareTo("C") > 0 && pair.getLeftCandidate().getName().compareTo("C") > 0) {
                String voteId = pair.getId() + "_" + user.getId() + "_VOTE";
                Vote newVote = new Vote(voteId, user.getId(), pair, VoteResult.NOT_SUBMITTED);
                votingRepository.initializeNewVote(newVote);
                votingRepository.setVoteResult(voteId, VoteResult.RIGHT_IS_BETTER);
            }
        }
        RowMapper<Vote> voteRowMapper = (rs, rowNum) -> new Vote(
                rs.getString("v_id"),
                rs.getLong("v_uid"),
                new Pair(
                        rs.getLong("p_id"),
                        rs.getLong("p_eid"),
                        new Candidate(
                                rs.getLong("lc_id"),
                                rs.getLong("p_eid"),
                                rs.getString("lc_name"),
                                rs.getString("lc_desc")
                        ),
                        new Candidate(
                                rs.getLong("rc_id"),
                                rs.getLong("p_eid"),
                                rs.getString("rc_name"),
                                rs.getString("rc_desc")
                        )
                ),
                VoteResult.valueOf(rs.getString("v_res"))
        );
        String votesQuery =
                "SELECT v.id as v_id, v.\"user\" as v_uid, v.vote_result as v_res, " +
                "       p.id as p_id, p.election as p_eid, " +
                "       lc.id as lc_id, lc.name as lc_name, lc.description as lc_desc, " +
                "       rc.id as rc_id, rc.name as rc_name, rc.description as rc_desc " +
                "FROM votes v " +
                "    JOIN pairs p on v.pair = p.id " +
                "    JOIN candidates lc on lc.id = p.left_candidate " +
                "    JOIN candidates rc on rc.id = p.right_candidate";
        List<Vote> votesBefore = jdbcTemplate.query(votesQuery, voteRowMapper);

        for (Pair pair : pairs) {
            if (pair.getRightCandidate().getName().equals("C") && pair.getLeftCandidate().getName().equals("D")) {
                String voteId = pair.getId() + "_" + user.getId() + "_VOTE";
                Vote newVote = new Vote(voteId, user.getId(), pair, VoteResult.NOT_SUBMITTED);
                votingRepository.initializeNewVote(newVote);
                votingService.submitVote(new SubmitVoteForm(voteId, false), user.getId());
            }
        }
        List<Vote> votesAfter = jdbcTemplate.query(votesQuery, voteRowMapper);

        String[] votesBeforeString = votesBefore.stream().map(vote ->
                vote.getPair().getRightCandidate().getName() + " > " + vote.getPair().getLeftCandidate().getName())
                .toArray(String[]::new);

        String[] votesAfterString = votesAfter.stream().map(vote ->
                vote.getPair().getRightCandidate().getName() + " > " + vote.getPair().getLeftCandidate().getName())
                .toArray(String[]::new);

        Assert.assertArrayEquals(new String[]{"A > B", "A > C", "B > C", "D > E"}, votesBeforeString);
        Assert.assertArrayEquals(new String[]{"A > B", "A > C", "B > C", "A > D", "B > D",
                "C > D", "A > E", "B > E", "C > E", "D > E"}, votesAfterString);
    }
}
