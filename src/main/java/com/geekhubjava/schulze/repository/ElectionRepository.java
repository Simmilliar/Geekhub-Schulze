package com.geekhubjava.schulze.repository;

import com.geekhubjava.schulze.model.Candidate;
import com.geekhubjava.schulze.model.Election;
import com.geekhubjava.schulze.model.response.Page;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ElectionRepository {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    // TODO: Maybe would be better to DI this.
    private final RowMapper<Election> electionRowMapper =
            (rs, rowNum) -> new Election(
                    rs.getLong("id"), rs.getLong("author"),
                    rs.getString("title"), rs.getString("description"),
                    rs.getBoolean("is_closed"), rs.getString("share_id")
            );

    public ElectionRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public boolean isTitleAlreadyExists(String title) {
        final String isTitleExistsSql = "SELECT COUNT(*) FROM elections WHERE title = :title";

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("title", title);
        int count = Objects.requireNonNull(namedParameterJdbcTemplate.queryForObject(
                isTitleExistsSql, namedParameters, Integer.TYPE));
        return count > 0;
    }

    public void insertNewElection(Election election) {
        final String createElectionSql =
                "INSERT INTO elections(author, title, description, is_closed, share_id) " +
                "VALUES (:author, :title, :description, :isClosed, :shareId)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("author", election.getAuthorId())
                .addValue("title", election.getTitle())
                .addValue("description", election.getDescription())
                .addValue("isClosed", election.isClosed())
                .addValue("shareId", election.getShareId());
        namedParameterJdbcTemplate.update(createElectionSql, namedParameters, keyHolder);
        int electionId = (int) Objects.requireNonNull(keyHolder.getKeys()).get("id");
        election.setId(electionId);
    }

    public void generatePairsForElection(long electionId) {
        final String generatePairsForElection =
                "INSERT INTO pairs(election, left_candidate, right_candidate) " +
                "SELECT c1.election, c1.id, c2.id " +
                "FROM candidates c1 CROSS JOIN candidates c2 " +
                "WHERE c1.election = :electionId AND c2.election = :electionId AND c1.id > c2.id";

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("electionId", electionId);
        namedParameterJdbcTemplate.update(generatePairsForElection, namedParameters);
    }

    public Optional<Election> getElectionByShareId(String shareId) {
        final String selectElectionByShareIdSql =
                "SELECT id, author, title, description, is_closed, share_id " +
                "FROM elections WHERE share_id = :shareId";

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("shareId", shareId);
        List<Election> elections = namedParameterJdbcTemplate.query(
                selectElectionByShareIdSql, namedParameters, electionRowMapper);
        return Optional.ofNullable(DataAccessUtils.singleResult(elections));
    }

    public List<Election> getElectionsByAuthorId(long authorId) {
        final String selectElectionByAuthorIdSql =
                "SELECT id, author, title, description, is_closed, share_id " +
                "FROM elections WHERE author = :authorId";

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("authorId", authorId);
        return namedParameterJdbcTemplate.query(selectElectionByAuthorIdSql, namedParameters, electionRowMapper);
    }

    public Page<Election> getElectionsByAuthorIdPaged(long authorId, int page, int perPage) {
        final String selectElectionByAuthorIdPagedSql =
                "SELECT id, author, title, description, is_closed, share_id, COUNT(*) OVER() AS rows_total " +
                "FROM elections WHERE author = :authorId LIMIT :limit OFFSET :offset";

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("authorId", authorId)
                .addValue("limit", perPage)
                .addValue("offset", perPage * (page - 1));
        return namedParameterJdbcTemplate.query(selectElectionByAuthorIdPagedSql, namedParameters, rs -> {
            List<Election> elections = new LinkedList<>();
            int rowNum = 1;
            int totalElections = 0;
            if (rs.next()) {
                totalElections = rs.getInt("rows_total");
                elections.add(electionRowMapper.mapRow(rs, rowNum));
                rowNum++;
            }
            while(rs.next()) {
                elections.add(electionRowMapper.mapRow(rs, rowNum));
                rowNum++;
            }
            return new Page<>(page, perPage, totalElections, elections);
        });
    }

    public List<Election> getParticipationsByUserId(long userId) {
        final String selectElectionByUserIdSql =
                "SELECT DISTINCT ON (e.id) e.id, e.author, e.title, e.description, e.is_closed, e.share_id " +
                "FROM elections e " +
                "   JOIN pairs p ON p.election = e.id " +
                "   JOIN votes v ON v.pair = p.id " +
                "WHERE v.\"user\" = :userId";

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("userId", userId);
        return namedParameterJdbcTemplate.query(selectElectionByUserIdSql, namedParameters, electionRowMapper);
    }

    public Page<Election> getParticipationsByUserIdPaged(long userId, int page, int perPage) {
        final String selectElectionByUserIdPagedSql =
                "SELECT e.id, e.author, e.title, e.description, " +
                "   e.is_closed, e.share_id, COUNT(*) OVER() AS rows_total " +
                "FROM elections e " +
                "   JOIN pairs p ON p.election = e.id " +
                "   JOIN votes v ON v.pair = p.id " +
                "WHERE v.\"user\" = :userId GROUP BY e.id LIMIT :limit OFFSET :offset";

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("limit", perPage)
                .addValue("offset", perPage * (page - 1));
        return namedParameterJdbcTemplate.query(selectElectionByUserIdPagedSql, namedParameters, rs -> {
            List<Election> elections = new LinkedList<>();
            int rowNum = 1;
            int totalElections = 0;
            if (rs.next()) {
                totalElections = rs.getInt("rows_total");
                elections.add(electionRowMapper.mapRow(rs, rowNum));
                rowNum++;
            }
            while(rs.next()) {
                elections.add(electionRowMapper.mapRow(rs, rowNum));
                rowNum++;
            }
            return new Page<>(page, perPage, totalElections, elections);
        });
    }

    public void closeElection(String shareId) {
        final String closeElectionSql = "UPDATE elections SET is_closed = TRUE WHERE share_id = :shareId";

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("shareId", shareId);
        namedParameterJdbcTemplate.update(closeElectionSql, namedParameters);
    }
}
