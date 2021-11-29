package com.toysocialnetworkgui.repository.db;

import com.toysocialnetworkgui.domain.Message;
import com.toysocialnetworkgui.validator.MessageValidator;
import com.toysocialnetworkgui.validator.Validator;

import java.sql.*;
import java.time.LocalDateTime;

public class MessageDbRepo {
    private final String url, username, password, messagesTable;
    private final Validator<Message> validator;

    public MessageDbRepo(String url, String username, String password, MessageValidator validator, String messagesTable) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.messagesTable = messagesTable;
        this.validator = validator;
        String sql = "CREATE TABLE IF NOT EXISTS " + messagesTable +
                "(id serial, " +
                " sender varchar NOT NULL," +
                " messagetext varchar NOT NULL," +
                " sentdate varchar NOT NULL," +
                " idmsgrepliedto int DEFAULT NULL," +
                " PRIMARY KEY (id)," +
                " FOREIGN KEY (sender) REFERENCES users (email) ON DELETE CASCADE," +
                " FOREIGN KEY (idmsgrepliedto) REFERENCES messages (id) ON DELETE CASCADE" +
                ");" +
                " CREATE UNIQUE index IF NOT EXISTS " + messagesTable + "_id_uindex ON " +
                messagesTable + " (id);";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
    }

    /**
     * Validates and saves a message in the database
     * @param message - the message to be saved
     */
    public Message save(Message message) {
        validator.validate(message);
        String sql = "INSERT INTO " + messagesTable + " (sender, messagetext, sentdate, idmsgrepliedto) VALUES (?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, message.getSender());
            ps.setString(2, message.getMessage());
            ps.setString(3, String.valueOf(LocalDateTime.now()));
            if (message.isReply())
                ps.setInt(4, message.getIdMsgRepliedTo());
            else
                ps.setNull(4, Types.INTEGER);
            ps.executeUpdate();
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
            if (res.getString("idmsgrepliedto") == null)
                message = new Message(res.getString("sender"), res.getString("messagetext"));
            else {
                message = new Message(res.getString("sender"), res.getString("messagetext"), res.getInt("idmsgrepliedto"));
            }
            message.setDate(LocalDateTime.parse(res.getString("sentdate")));
            message.setID(id);
            return message;
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
    }

    /**
     * @return int - the number of messages saved in the database
     */
    public int size() {
        String sql = "SELECT COUNT(*) AS size FROM " + messagesTable;
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
     * Removes all messages from database
     */
    public void clear() {
        String sql = "DELETE FROM " + messagesTable;
        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
    }
}
