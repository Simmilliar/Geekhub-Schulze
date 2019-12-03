package com.geekhubjava.schulze.repository;

import com.geekhubjava.schulze.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class UserRepository {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public UserRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public void createNewUser(User user) {
        String createUserSql = "INSERT INTO users(login, password_hash) VALUES (:login, :passwordHash)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("login", user.getLogin())
                .addValue("passwordHash", user.getPasswordHash());
        namedParameterJdbcTemplate.update(createUserSql, namedParameters, keyHolder);
        int userId = (int) Objects.requireNonNull(keyHolder.getKeys()).get("id");
        user.setId(userId);
    }

    public boolean isLoginAlreadyExists(String login) {
        String isLoginExistsSql = "SELECT COUNT(*) FROM users WHERE login = :login";

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("login", login);
        int count = Objects.requireNonNull(namedParameterJdbcTemplate.queryForObject(
                isLoginExistsSql, namedParameters, Integer.TYPE));
        return count > 0;
    }

    public Optional<User> getUserByLogin(String login) {
        String selectUserByLoginSql = "SELECT id, login, password_hash FROM users WHERE login = :login";

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("login", login);
        List<User> users = namedParameterJdbcTemplate.query(selectUserByLoginSql, namedParameters,
                (rs, rowNum) -> new User(rs.getInt("id"), rs.getString("login"),
                        rs.getString("password_hash"))
        );
        if (users.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(users.get(0));
        }
    }
}
