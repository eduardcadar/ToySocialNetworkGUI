package com.toysocialnetworkgui.repository.db;

import com.toysocialnetworkgui.domain.MessageReceiver;
import com.toysocialnetworkgui.validator.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageReceiverDbRepo {
    private final String url, username, password, receiversTable;
    private final Validator<MessageReceiver> validator;

    public MessageReceiverDbRepo(String url, String username, String password, Validator<MessageReceiver> validator, String receiversTable) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.receiversTable = receiversTable;
        this.validator = validator;
        String sql = "CREATE TABLE IF NOT EXISTS " + receiversTable +
                "(idmessage int NOT NULL," +
                " receiver varchar NOT NULL," +
                " PRIMARY KEY (idmessage, receiver)," +
                " FOREIGN KEY (idmessage) REFERENCES messages (id) ON DELETE CASCADE," +
                " FOREIGN KEY (receiver) REFERENCES users (email) ON DELETE CASCADE" +
                ");";

        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
    }

    /**
     * Validates and saves a MessageReceiver in the database
     * @param messageReceiver - the MessageReceiver to be saved
     */
    public void save(MessageReceiver messageReceiver) {
        validator.validate(messageReceiver);
        String sql = "INSERT INTO " + receiversTable + " (idmessage, receiver) VALUES (?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, messageReceiver.getIdMessage());
            ps.setString(2, messageReceiver.getReceiver());
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
    }

    /**
     * @return int - the number of receivers saved in the database
     */
    public int size() {
        String sql = "SELECT COUNT(*) AS size FROM " + receiversTable;
        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet res = ps.executeQuery();
            if (res.next()) {
                return res.getInt("size");
            }
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
        return 0;
    }

    /**
     * Removes all receivers from database
     */
    public void clear() {
        String sql = "DELETE FROM " + receiversTable;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
    }

    /**
     * Returns a list with the ids of the messages received by a user
     * @param email the email of the user
     * @return list of ids
     */
    public List<Integer> getMessageIdsReceivedBy(String email) {
        List<Integer> messages = new ArrayList<>();
        String sql = "SELECT idmessage FROM " + receiversTable +
                " WHERE receiver = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet res = ps.executeQuery();
            while (res.next())
                messages.add(res.getInt("idMessage"));
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
        return messages;
    }

    public List<String> getMessageReceivers(int idMessage) {
        List<String> receivers = new ArrayList<>();
        String sql = "SELECT receiver FROM " + receiversTable +
                " WHERE idmessage = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idMessage);
            ResultSet res = ps.executeQuery();
            while (res.next())
                receivers.add(res.getString("receiver"));
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
        return receivers;
    }
}
