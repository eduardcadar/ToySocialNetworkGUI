package com.toysocialnetworkgui.repository.db;

import com.toysocialnetworkgui.domain.ConversationParticipant;
import com.toysocialnetworkgui.repository.observer.Observable;
import com.toysocialnetworkgui.validator.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConversationParticipantDbRepo implements Observable {
    private final String url, username, password, participantsTable;
    private final Validator<ConversationParticipant> validator;

    public ConversationParticipantDbRepo(String url, String username, String password, Validator<ConversationParticipant> validator, String participantsTable) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.participantsTable = participantsTable;
        this.validator = validator;
        String sql = "CREATE TABLE IF NOT EXISTS " + participantsTable +
                "(idconversation int NOT NULL," +
                " participant varchar NOT NULL," +
                " PRIMARY KEY (idconversation, participant)," +
                " FOREIGN KEY (idconversation) REFERENCES conversations (id) ON DELETE CASCADE," +
                " FOREIGN KEY (participant) REFERENCES users (email) ON DELETE CASCADE" +
                ");";

        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
    }

    /**
     * Validates and saves a ConversationParticipant in the database
     * @param conversationParticipant - the ConversationParticipant to be saved
     */
    public void save(ConversationParticipant conversationParticipant) {
        validator.validate(conversationParticipant);
        String sql = "INSERT INTO " + participantsTable + " (idconversation, participant) VALUES (?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, conversationParticipant.getIdConversation());
            ps.setString(2, conversationParticipant.getParticipant());
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
    }

    /**
     * @return int - the number of participants saved in the database
     */
    public int size() {
        String sql = "SELECT COUNT(*) AS size FROM " + participantsTable;
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
     * Removes all participants from database
     */
    public void clear() {
        String sql = "DELETE FROM " + participantsTable;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.executeUpdate();
            notifyObservers();
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
    }

    /**
     * Returns a list with the ids of the conversations of a user
     * @param email the email of the user
     * @return list of ids
     */
    public List<Integer> getUserConversations(String email) {
        List<Integer> conversations = new ArrayList<>();
        String sql = "SELECT idconversation FROM " + participantsTable +
                " WHERE participant = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet res = ps.executeQuery();
            while (res.next())
                conversations.add(res.getInt("idConversation"));
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
        return conversations;
    }

    public List<String> getConversationParticipants(int idConversation) {
        List<String> participants = new ArrayList<>();
        String sql = "SELECT participant FROM " + participantsTable +
                " WHERE idconversation = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idConversation);
            ResultSet res = ps.executeQuery();
            while (res.next())
                participants.add(res.getString("participant"));
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
        return participants;
    }

    public List<ConversationParticipant> getAll() {
        List<ConversationParticipant> conversationParticipants = new ArrayList<>();
        String sql = "SELECT * FROM " + participantsTable;
        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet res = ps.executeQuery();
            while (res.next())
                conversationParticipants.add(new ConversationParticipant
                        (res.getInt("idconversation"), res.getString("participant")));
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return conversationParticipants;
    }
}
