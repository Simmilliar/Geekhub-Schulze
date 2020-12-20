package com.geekhubjava.schulze.repository;

import com.geekhubjava.schulze.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class VotingRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    // TODO: Maybe would be better to DI this.
    private final RowMapper<Pair> pairRowMapper =
            (rs, rowNum) -> new Pair(
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
    private final RowMapper<Vote> voteRowMapper =
            (rs, rowNum) -> new Vote(
                    rs.getString("v_id"),
                    rs.getLong("v_user"),
                    pairRowMapper.mapRow(rs, rowNum),
                    VoteResult.valueOf(rs.getString("v_vote_result"))
            );

    @Autowired
    public VotingRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public Optional<Pair> getNewPairForUser(long electionId, long userId) {
        final String selectPairForUserSql =
                "SELECT p.id AS p_id, p.election AS p_election, " +
                "   cl.id AS cl_id, cl.name AS cl_name, cl.description AS cl_description, " +
                "   cr.id AS cr_id, cr.name AS cr_name, cr.description AS cr_description " +
                "FROM pairs p LEFT JOIN votes v ON v.pair = p.id " +
                "   JOIN candidates cl ON cl.id = p.left_candidate " +
                "   JOIN candidates cr ON cr.id = p.right_candidate " +
                "WHERE p.election = :electionId AND p.id NOT IN (SELECT pair FROM votes WHERE \"user\" = :userId) " +
                "GROUP BY p.id, cl.id, cr.id " +
                "ORDER BY COUNT(v.id) LIMIT 1";

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("electionId", electionId)
                .addValue("userId", userId);
        List<Pair> pairs = namedParameterJdbcTemplate.query(selectPairForUserSql, namedParameters, pairRowMapper);
        return Optional.ofNullable(DataAccessUtils.singleResult(pairs));
    }

    public void initializeNewVote(Vote vote) {
        final String initializeNewVoteSql =
                "INSERT INTO votes(id, \"user\", pair, vote_result) " +
                "VALUES (:id, :userId, :pairId, 'NOT_SUBMITTED')";

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("id", vote.getId())
                .addValue("userId", vote.getUserId())
                .addValue("pairId", vote.getPair().getId());
        namedParameterJdbcTemplate.update(initializeNewVoteSql, namedParameters);
    }

    public Optional<Vote> getNotSubmittedVoteForUser(long electionId, long userId) {
        final String getNotSubmittedVoteForUser =
                "SELECT v.id AS v_id, v.\"user\" AS v_user, v.vote_result AS v_vote_result, " +
                "   p.id AS p_id, p.election AS p_election, " +
                "   cl.id AS cl_id, cl.name AS cl_name, cl.description AS cl_description, " +
                "   cr.id AS cr_id, cr.name AS cr_name, cr.description AS cr_description " +
                "FROM votes v JOIN pairs p ON v.pair = p.id " +
                "   JOIN candidates cl ON p.left_candidate = cl.id " +
                "   JOIN candidates cr ON p.right_candidate = cr.id " +
                "WHERE v.vote_result = 'NOT_SUBMITTED' AND p.election = :electionId AND v.\"user\" = :userId " +
                "LIMIT 1";

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("electionId", electionId);
        List<Vote> votes = namedParameterJdbcTemplate.query(getNotSubmittedVoteForUser, namedParameters, voteRowMapper);
        return Optional.ofNullable(DataAccessUtils.singleResult(votes));
    }

    public boolean isVoteExists(String voteId) {
        final String isVoteExistsSql = "SELECT COUNT(*) FROM votes WHERE id = :voteId";

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("voteId", voteId);
        int count = Objects.requireNonNull(namedParameterJdbcTemplate.queryForObject(
                isVoteExistsSql, namedParameters, Integer.TYPE));
        return count > 0;
    }

    public boolean canUserSubmitVote(String voteId, long userId) {
        final String canUserSubmitVoteSql =
                "SELECT COUNT(*) FROM votes " +
                "WHERE id = :voteId AND \"user\" = :userId AND vote_result = 'NOT_SUBMITTED'";

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("voteId", voteId)
                .addValue("userId", userId);
        int count = Objects.requireNonNull(namedParameterJdbcTemplate.queryForObject(
                canUserSubmitVoteSql, namedParameters, Integer.TYPE));
        return count > 0;
    }

    public Optional<Vote> getVoteById(String voteId) {
        final String getVoteByIdSql =
                "SELECT v.id AS v_id, v.\"user\" AS v_user, v.vote_result AS v_vote_result, " +
                "   p.id AS p_id, p.election AS p_election, " +
                "   cl.id AS cl_id, cl.name AS cl_name, cl.description AS cl_description, " +
                "   cr.id AS cr_id, cr.name AS cr_name, cr.description AS cr_description " +
                "FROM votes v JOIN pairs p ON v.pair = p.id " +
                "   JOIN candidates cl ON p.left_candidate = cl.id " +
                "   JOIN candidates cr ON p.right_candidate = cr.id " +
                "WHERE v.id = :voteId";

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("voteId", voteId);
        List<Vote> votes = namedParameterJdbcTemplate.query(getVoteByIdSql, namedParameters, voteRowMapper);
        return Optional.ofNullable(DataAccessUtils.singleResult(votes));
    }

    public void setVoteResult(String voteId, VoteResult voteResult) {
        final String submitVoteSql = "UPDATE votes SET vote_result = :voteResult WHERE id = :voteId";

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("voteId", voteId)
                .addValue("voteResult", voteResult.toString());
        namedParameterJdbcTemplate.update(submitVoteSql, namedParameters);
    }

    public List<Vote> getMissingVotesForCase(long betterCandidateId, long worseCandidateId, long userId) {
        final String getMissingVotesForCaseSql =
                "WITH wtwc AS ( " +
                "    SELECT p.left_candidate AS wtwcid " +
                "    FROM votes v JOIN pairs p ON v.pair = p.id " +
                "    WHERE v.\"user\" = :userId " +
                "      AND p.right_candidate = :worseCandidate " +
                "      AND v.vote_result = 'RIGHT_IS_BETTER' " +
                "    UNION " +
                "    SELECT p.right_candidate AS wtwcid " +
                "    FROM votes v JOIN pairs p ON v.pair = p.id " +
                "    WHERE v.\"user\" = :userId " +
                "      AND p.left_candidate = :worseCandidate " +
                "      AND v.vote_result = 'LEFT_IS_BETTER' " +
                "), btbc AS ( " +
                "    SELECT p.left_candidate AS btbcid " +
                "    FROM votes v JOIN pairs p ON v.pair = p.id " +
                "    WHERE v.\"user\" = :userId " +
                "      AND p.right_candidate = :betterCandidate " +
                "      AND v.vote_result = 'LEFT_IS_BETTER' " +
                "    UNION " +
                "    SELECT p.right_candidate AS btbcid " +
                "    FROM votes v JOIN pairs p ON v.pair = p.id " +
                "    WHERE v.\"user\" = :userId " +
                "      AND p.left_candidate = :betterCandidate " +
                "      AND v.vote_result = 'RIGHT_IS_BETTER' " +
                ") " +
                "SELECT p.id AS pair_id, 'RIGHT_IS_BETTER' AS vote_result " +
                "FROM pairs p " +
                "WHERE p.right_candidate IN (SELECT btbcid FROM btbc UNION SELECT :betterCandidate) " +
                "  AND p.left_candidate IN (SELECT wtwcid FROM wtwc UNION SELECT :worseCandidate) " +
                "UNION " +
                "SELECT p.id as pair_id, 'LEFT_IS_BETTER' AS vote_result " +
                "FROM pairs p " +
                "WHERE p.left_candidate  IN (SELECT btbcid FROM btbc UNION SELECT :betterCandidate) " +
                "  AND p.right_candidate IN (SELECT wtwcid FROM wtwc UNION SELECT :worseCandidate)";

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("betterCandidate", betterCandidateId)
                .addValue("worseCandidate", worseCandidateId);
        return namedParameterJdbcTemplate.query(getMissingVotesForCaseSql, namedParameters,
                (rs, rowNum) -> new Vote(
                        null,
                        userId,
                        new Pair(rs.getLong("pair_id"), 0, null, null),
                        VoteResult.valueOf(rs.getString("vote_result"))
                )
        );
    }

    public void batchInsertVotes(List<Vote> votes) {
        final String batchInsertVotesSql =
                "INSERT INTO votes(id, \"user\", pair, vote_result) " +
                "VALUES (:voteId, :userId, :pairId, :voteResult) " +
                "ON CONFLICT (\"user\", pair) DO NOTHING";

        SqlParameterSource[] namedParameters = votes.stream()
                .map(v -> new MapSqlParameterSource()
                        .addValue("voteId", v.getId())
                        .addValue("userId", v.getUserId())
                        .addValue("pairId", v.getPair().getId())
                        .addValue("voteResult", v.getVoteResult().toString())
                ).toArray(SqlParameterSource[]::new);
        namedParameterJdbcTemplate.batchUpdate(batchInsertVotesSql, namedParameters);
    }

    public List<Vote> getVotesForElection(long electionId) {
        final String getVotesForElectionSql =
                "SELECT v.id AS v_id, v.\"user\" AS v_user, v.vote_result AS v_vote_result, " +
                "   p.id AS p_id, p.election AS p_election, " +
                "   cl.id AS cl_id, cl.name AS cl_name, cl.description AS cl_description, " +
                "   cr.id AS cr_id, cr.name AS cr_name, cr.description AS cr_description " +
                "FROM votes v JOIN pairs p ON v.pair = p.id " +
                "   JOIN candidates cl ON p.left_candidate = cl.id " +
                "   JOIN candidates cr ON p.right_candidate = cr.id " +
                "WHERE p.election = :electionId AND v.vote_result != 'NOT_SUBMITTED'";

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("electionId", electionId);
        return namedParameterJdbcTemplate.query(getVotesForElectionSql, namedParameters, voteRowMapper);
    }

    public List<VotingSummaryData> getVotingSummaryPerPairForElection(long electionId) {
        final String getVotingSummarySql =
                "SELECT cl.name AS cl_name, cr.name AS cr_name, " +
                "   COUNT(v.id) FILTER (WHERE v.vote_result = 'LEFT_IS_BETTER') AS left_votes, " +
                "   COUNT(v.id) FILTER (WHERE v.vote_result = 'RIGHT_IS_BETTER') AS right_votes " +
                "FROM pairs p " +
                "   JOIN votes v ON v.pair = p.id " +
                "   JOIN candidates cl ON p.left_candidate = cl.id " +
                "   JOIN candidates cr ON p.right_candidate = cr.id " +
                "WHERE p.election = :electionId AND v.vote_result != 'NOT_SUBMITTED' " +
                "GROUP BY cl.id, cr.id";

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("electionId", electionId);
        return namedParameterJdbcTemplate.query(getVotingSummarySql, namedParameters,
                (rs, rowNum) -> new VotingSummaryData(
                        rs.getString("cl_name"),
                        rs.getString("cr_name"),
                        rs.getInt("left_votes"),
                        rs.getInt("right_votes")
                ));
    }
}
