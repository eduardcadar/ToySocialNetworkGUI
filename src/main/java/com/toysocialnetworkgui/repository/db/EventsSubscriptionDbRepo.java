package com.toysocialnetworkgui.repository.db;

import com.toysocialnetworkgui.domain.Event;
import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.repository.RepoException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventsSubscriptionDbRepo {
    private final String url;
    private final String username;
    private final String password;
    private final String tableName;

    public EventsSubscriptionDbRepo(String url,String username, String password, String subscriptionTable){
        this.url = url;
        this.username = username;
        this.password = password;
        this.tableName = subscriptionTable;

        String sql = "CREATE TABLE IF NOT EXISTS " + subscriptionTable +
                "(event_id int not null, " +
                "user_email varchar not null, " +
                "PRIMARY KEY(event_id, user_email), " +
                " FOREIGN KEY (event_id) references  events(id) ON DELETE CASCADE," +
                " FOREIGN KEY (user_email) references users(email) ON DELETE CASCADE " +
                ")";
        try(Connection connection = DriverManager.getConnection(url,username,password)){
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }


    }


    public void addSubscriber(Integer evId, String userEmail){
        if(existsSubscription(evId, userEmail)){
            throw new RepoException("You already subscribed to this event!");

        }
        String sql = "INSERT INTO " + tableName + " (event_id, user_email) values (?, ?) ";
        try(Connection connection = DriverManager.getConnection(url, username, password)){
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, evId);
            ps.setString(2, userEmail);
            ps.executeUpdate();
           } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
    }

    private boolean existsSubscription(Integer evId, String userEmail) {
        String sql = "SELECT * FROM  " + tableName + " WHERE  event_id = ? AND user_email= ?";
        try(Connection connection = DriverManager.getConnection(url, username, password)){
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, evId);
            ps.setString(2, userEmail);
            ResultSet resultSet = ps.executeQuery();
            if(resultSet.next())
                return true;
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
        return false;

    }

    public void removeSubscriber(Integer evId, String userEmail){
        String sql = "DELETE FROM " + tableName + " WHERE event_id = ? AND user_email = ?) ";
        try(Connection connection = DriverManager.getConnection(url, username, password)){
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, evId);
            ps.setString(2, userEmail);
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
    }

    public List<Integer> getEventsForUser(String userEmail) {
        List<Integer> eventsId = new ArrayList<>();
        String sql = " SELECT event_id FROM "+ tableName + " WHERE user_email = ?";
        try(Connection connection = DriverManager.getConnection(url,username, password)){
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1,userEmail);
            ResultSet resultSet = ps.executeQuery();
            while(resultSet.next()){
                eventsId.add(resultSet.getInt("event_id"));

            }

        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
        return eventsId;
    }
}
