package com.geekhubjava.schulze.repository;

import com.geekhubjava.schulze.model.Candidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CandidatesRepository {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public CandidatesRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public void insertElectionCandidates(List<Candidate> candidates) {
        final String createCandidateSql =
                "INSERT INTO candidates(election, name, description) " +
                "VALUES (:electionId, :name, :description)";

        SqlParameterSource[] namedParameters = candidates.stream()
                .map(c -> new MapSqlParameterSource()
                        .addValue("electionId", c.getElectionId())
                        .addValue("name", c.getName())
                        .addValue("description", c.getDescription())
                ).toArray(SqlParameterSource[]::new);
        namedParameterJdbcTemplate.batchUpdate(createCandidateSql, namedParameters);
    }

    public List<Candidate> getElectionCandidates(long electionId) {
        final String getElectionCandidatesSql =
                "SELECT id, election, name, description FROM candidates WHERE election = :electionId";

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("electionId", electionId);
        return namedParameterJdbcTemplate.query(getElectionCandidatesSql, namedParameters,
                (rs, rowNum) -> new Candidate(
                        rs.getLong("id"), rs.getLong("election"),
                        rs.getString("name"), rs.getString("description")
                ));
    }
}
