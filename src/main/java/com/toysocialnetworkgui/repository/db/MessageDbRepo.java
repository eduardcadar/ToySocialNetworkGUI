package com.toysocialnetworkgui.repository.db;

import com.toysocialnetworkgui.domain.Message;
import com.toysocialnetworkgui.repository.observer.Observable;
import com.toysocialnetworkgui.validator.MessageValidator;
import com.toysocialnetworkgui.validator.Validator;
import javafx.application.Platform;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageDbRepo implements Observable {
    private final String url, username, password, messagesTable;
    private final Validator<Message> validator;

    public MessageDbRepo(String url, String username, String password, MessageValidator validator, String messagesTable) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.messagesTable = messagesTable;
        this.validator = validator;
//        String sql = "CREATE TABLE IF NOT EXISTS " + messagesTable +
//                "(id serial, " +
//                " idconversation int NOT NULL," +
//                " sender varchar NOT NULL," +
//                " messagetext varchar NOT NULL," +
//                " sentdate varchar NOT NULL," +
//                " PRIMARY KEY (id)," +
//                " FOREIGN KEY (idconversation) REFERENCES conversations (id) ON DELETE CASCADE," +
//                " FOREIGN KEY (sender) REFERENCES users (email) ON DELETE CASCADE" +
//                ");" +
//                " CREATE UNIQUE index IF NOT EXISTS " + messagesTable + "_id_uindex ON " +
//                messagesTable + " (id);";
//
//        try (Connection connection = DriverManager.getConnection(url, username, password);
//             PreparedStatement ps = connection.prepareStatement(sql)) {
//            ps.executeUpdate();
//        } catch (SQLException throwables) {
//            throw new DbException(throwables.getMessage());
//        }
    }

    /**
     * Validates and saves a message in the database
     * @param message - the message to be saved
     */
    public Message save(Message message) {
        validator.validate(message);
        String sql = "INSERT INTO " + messagesTable + " (idconversation, sender, messagetext, sentdate) VALUES (?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, message.getIdConversation());
            ps.setString(2, message.getSender());
            ps.setString(3, message.getMessage());
            ps.setString(4, String.valueOf(LocalDateTime.now()));
            ps.executeUpdate();
            Platform.runLater(this::notifyObservers);
            ResultSet res = ps.getGeneratedKeys();
            if (res.next())
                message.setID(res.getInt(1));
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
        return message;
    }

    /**
     * @param id - int - the id of the message to be returned
     * @return the message with the specified id,
     * null if no message has the given id
     */
    public Message getMessage(int id) {
        String sql = "SELECT * FROM " + messagesTable + " WHERE id = ?";
        Message message;
        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet res = ps.executeQuery();
            if (!res.next())
                return null;
            message = new Message(res.getInt("idconversation"), res.getString("sender"), res.getString("messagetext"));
            message.setDate(LocalDateTime.parse(res.getString("sentdate")));
            message.setID(id);
            return message;
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
    }

    /**
     * @param idConversation the id of the conversation
     * @return list with a conversation's messages
     */
    public List<Message> getConversationMessages(int idConversation) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM " + messagesTable + " WHERE idconversation = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idConversation);
            ResultSet res = ps.executeQuery();
            while (res.next()) {
                Message message = new Message(res.getInt("idconversation"), res.getString("sender"), res.getString("messagetext"));
                message.setDate(LocalDateTime.parse(res.getString("sentdate")));
                message.setID(res.getInt("id"));
                messages.add(message);
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return messages;
    }

    public List<Message> getConversationMessagesPage(int idConversation, int firstrow, int rowcount) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM " + messagesTable + " WHERE idconversation = ?" +
                " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idConversation);
            ps.setInt(2, firstrow);
            ps.setInt(3, rowcount);
            ResultSet res = ps.executeQuery();
            while (res.next()) {
                Message message = new Message(res.getInt("idconversation"), res.getString("sender"), res.getString("messagetext"));
                message.setDate(LocalDateTime.parse(res.getString("sentdate")));
                message.setID(res.getInt("id"));
                messages.add(message);
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return messages;
    }

    /**
     * @return int - the number of messages saved in the database
     */
    public int size() {
        String sql = "SELECT COUNT(*) AS size FROM " + messagesTable;
        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet res = ps.executeQuery();
            if (res.next())
                return res.getInt("size");
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
        return 0;
    }

    /**
     * @param idConversation id of the conversation
     * @return number of messages in the conversation
     */
    public int conversationSize(int idConversation) {
        String sql = "SELECT COUNT(*) AS size FROM " + messagesTable +
                " WHERE idconversation = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idConversation);
            ResultSet res = ps.executeQuery();
            if (res.next())
                return res.getInt("size");
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return 0;
    }

    /**
     * Removes all messages from database
     */
    public void clear() {
        String sql = "DELETE FROM " + messagesTable;
        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.executeUpdate();
            notifyObservers();
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
    }

    /**
     * Returns all messages saved
     * @return list of Message
     */
    public List<Message> getAll() {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM " + messagesTable;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet res = ps.executeQuery();
            while (res.next()) {
                Message message = new Message(res.getInt("idconversation"),
                        res.getString("sender"), res.getString("messagetext"));
                message.setDate(LocalDateTime.parse(res.getString("sentdate")));
                message.setID(res.getInt("id"));
                messages.add(message);
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return messages;
    }
}
