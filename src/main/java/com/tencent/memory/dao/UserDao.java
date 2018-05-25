package com.tencent.memory.dao;

import com.tencent.memory.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;


@Repository
public class UserDao extends JdbcDaoSupport {

    @Autowired
    public UserDao(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public User findById(Long uid) {
        return getJdbcTemplate().queryForObject("SELECT * FROM user WHERE id = ?", new UserRowMapper(), uid);
    }


    public class UserRowMapper implements RowMapper<User> {

        public User mapRow(ResultSet rs, int i) throws SQLException {
            int uid = rs.getInt("user.id");

            User u = new User();
            return u;
        }
    }
}
