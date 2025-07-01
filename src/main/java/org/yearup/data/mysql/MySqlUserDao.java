package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.yearup.data.UserDao;
import org.yearup.models.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlUserDao extends MySqlDaoBase implements UserDao
{
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public MySqlUserDao(DataSource dataSource)
    {
        super(dataSource);
        this.passwordEncoder = new BCryptPasswordEncoder(); // or inject if needed
    }

    @Override
    public User create(User newUser)
    {
        String sql = "INSERT INTO users (username, hashed_password, role) VALUES (?, ?, ?)";
        String hashedPassword = passwordEncoder.encode(newUser.getPassword());

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            ps.setString(1, newUser.getUsername());
            ps.setString(2, hashedPassword);
            ps.setString(3, newUser.getRole());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys())
            {
                if (keys.next())
                {
                    int userId = keys.getInt(1);
                    return new User(userId, newUser.getUsername(), "", newUser.getRole());
                }
                else
                {
                    throw new SQLException("User ID not generated.");
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error creating user", e);
        }
    }

    @Override
    public List<User> getAll()
    {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet row = statement.executeQuery())
        {
            while (row.next())
            {
                users.add(mapRow(row));
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error retrieving users", e);
        }

        return users;
    }

    @Override
    public User getUserById(int id)
    {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setInt(1, id);
            try (ResultSet row = statement.executeQuery())
            {
                if (row.next())
                {
                    return mapRow(row);
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error retrieving user by ID", e);
        }

        return null;
    }

    @Override
    public User getByUserName(String username)
    {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setString(1, username);
            try (ResultSet row = statement.executeQuery())
            {
                if (row.next())
                {
                    return mapRow(row);
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error retrieving user by username", e);
        }

        return null;
    }

    @Override
    public int getIdByUsername(String username)
    {
        User user = getByUserName(username);
        return user != null ? user.getId() : -1;
    }

    @Override
    public boolean exists(String username)
    {
        return getByUserName(username) != null;
    }

    private User mapRow(ResultSet row) throws SQLException
    {
        int userId = row.getInt("user_id");
        String username = row.getString("username");
        String hashedPassword = row.getString("hashed_password");
        String role = row.getString("role");

        return new User(userId, username, hashedPassword, role);
    }
}