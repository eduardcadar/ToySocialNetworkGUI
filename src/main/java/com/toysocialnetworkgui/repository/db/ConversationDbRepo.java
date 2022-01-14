package com.toysocialnetworkgui.repository.db;

import com.toysocialnetworkgui.domain.Conversation;
import com.toysocialnetworkgui.repository.observer.Observable;

import java.sql.*;
import java.time.LocalDateTime;

public class ConversationDbRepo implements Observable {
    private final String url, username, password, conversationsTable;

    public ConversationDbRepo(String url, String username, String password, String conversationsTable) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.conversationsTable = conversationsTable;

        String sql = "CREATE TABLE IF NOT EXISTS " + conversationsTable +
                "(id serial," +
                " datecreated varchar," +
                " PRIMARY KEY (id)" +
                ");";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
    }

    public Conversation getConversation(int idConversation) {
        Conversation conversation = new Conversation();
        conversation.setID(idConversation);
        String sql = "SELECT * FROM " + conversationsTable + " WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idConversation);
            ResultSet res = ps.executeQuery();
            if (!res.next())
                return null;
            conversation.setDate(LocalDateTime.parse(res.getString("datecreated")));
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return conversation;
    }

    public Conversation save(Conversation conversation) {
        String sql = "INSERT INTO " + conversationsTable + " (datecreated) VALUES (?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, String.valueOf(LocalDateTime.now()));
            ps.executeUpdate();
            ResultSet res = ps.getGeneratedKeys();
            if (res.next())
                conversation.setID(res.getInt(1));
            notifyObservers();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return conversation;
    }

    /**
     * Removes all conversations from database
     */
    public void clear() {
        String sql = "DELETE FROM " + conversationsTable;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.executeUpdate();
            notifyObservers();
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
    }
}
