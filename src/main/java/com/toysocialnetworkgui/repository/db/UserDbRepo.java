package com.toysocialnetworkgui.repository.db;

import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.repository.RepoException;
import com.toysocialnetworkgui.repository.UserRepository;
import com.toysocialnetworkgui.repository.observer.Observable;
import com.toysocialnetworkgui.validator.Validator;
import javafx.application.Platform;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDbRepo implements Observable, UserRepository {
    private final String url, username, password, usersTable;
    private final Validator<User> validator;

    public UserDbRepo(String url, String username, String password, Validator<User> validator, String usersTable) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        this.usersTable = usersTable;
//        String sql = "CREATE TABLE IF NOT EXISTS " + usersTable +
//                "(firstname varchar NOT NULL," +
//                " lastname varchar NOT NULL, " +
//                " email varchar NOT NULL, " +
//                " PRIMARY KEY (email) " +
//                ")";
//
//        String updateTable = "ALTER TABLE " + usersTable +
//                " ADD COLUMN IF NOT EXISTS password varchar DEFAULT '000000'";
//
//        String updateTableAddProfilePicture = "ALTER TABLE " + usersTable +
//                " ADD COLUMN IF NOT EXISTS profile_path varchar DEFAULT '/profile/anonymous.png'";
//
//        try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password)){
//             PreparedStatement ps = connection.prepareStatement(sql);
//            ps.executeUpdate();
//            PreparedStatement updateStatement = connection.prepareStatement(updateTable);
//            updateStatement.executeUpdate();
//
//            PreparedStatement updateStatementAddProfile = connection.prepareStatement(updateTableAddProfilePicture);
//            updateStatementAddProfile.executeUpdate();
//
//
//        } catch (SQLException e) {
//            throw new DbException(e.getMessage());
//        }
    }

    /**
     * Validates and adds a user to the database
     * @param u - the user to be added
     */
    @Override
    public void save(User u) {
        validator.validate(u);
        if (getUser(u.getEmail()) != null)
            throw new RepoException("There is already a user with same email");
        String sql = "INSERT INTO " + usersTable + " (firstname, lastname, email, password, profile_path) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, u.getFirstName());
            ps.setString(2, u.getLastName());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getPassword());
            ps.setString(5, u.getProfilePicturePath());
            ps.executeUpdate();
            Platform.runLater(this::notifyObservers);
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    /**
     * @param email - String with the email of the user to be returned
     * @return the user with the email given as a parameter,
     * null if no user in the database has the given email
     */
    @Override
    public User getUser(String email) {
        String sql = "SELECT * FROM " + usersTable + " WHERE email = ?";
        User us;
        try (Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet res = ps.executeQuery();
            if (!res.next())
                return null;
            us = new User(res.getString("firstname"), res.getString("lastname"), res.getString("email"), res.getString("password"), res.getString("profile_path"));
            return us;
        }
        catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    /**
     * Removes a user from the database
     * @param email - String the email of the user to be removed
     */
    @Override
    public void remove(String email) {
        getUser(email);
        String sql = "DELETE FROM " + usersTable + " WHERE email = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.executeUpdate();
            notifyObservers();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    /**
     * @return the number of users saved in the database
     */
    @Override
    public int size() {
        String sql = "SELECT COUNT(*) AS size FROM " + usersTable;
        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet res = ps.executeQuery();
            if (res.next()) {
                return res.getInt("size");
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return 0;
    }

    /**
     * Removes all users from the database
     */
    @Override
    public void clear() {
        String sql = "DELETE FROM " + usersTable;
        try (Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.executeUpdate();
            notifyObservers();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    /**
     * @return all the users saved in the database
     */
    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM " + usersTable;
        try (Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet res = ps.executeQuery();
            while (res.next()) {
                String firstname = res.getString("firstname");
                String lastname = res.getString("lastname");
                String email = res.getString("email");
                String password = res.getString("password");
                String profile_path = res.getString("profile_path");
                users.add(new User(firstname, lastname, email, password, profile_path));
            }
            return users;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    /**
     * @return true if the database has no users saved, false otherwise
     */
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Updates a user's first name, last name and password in the database
     * @param user - the user with the new attributes
     */
    @Override
    public User update(User user) {
        validator.validate(user);
        if (getUser(user.getEmail()) == null)
            throw new RepoException("Update failed");
        String sql = "UPDATE " + usersTable + " SET firstname = ?, lastname = ?, password = ?, profile_path = ? WHERE email = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getProfilePicturePath());
            ps.setString(5, user.getEmail());
            ps.executeUpdate();
            notifyObservers();
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
        return user;
    }
}
