package com.toysocialnetworkgui.repository.db;

import com.toysocialnetworkgui.domain.Event;
import com.toysocialnetworkgui.repository.RepoException;
import com.toysocialnetworkgui.repository.observer.Observable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventsSubscriptionDbRepo implements Observable {
    private final String url;
    private final String username;
    private final String password;
    private final String subscriptionsTable;
    private final String eventsTable;

    public EventsSubscriptionDbRepo(String url,String username, String password, String subscriptionTable, String eventsTable){
        this.url = url;
        this.username = username;
        this.password = password;
        this.subscriptionsTable = subscriptionTable;
        this.eventsTable = eventsTable;

//        String sql = "CREATE TABLE IF NOT EXISTS " + subscriptionTable +
//                "(event_id int not null, " +
//                "user_email varchar not null, " +
//                "PRIMARY KEY(event_id, user_email), " +
//                " FOREIGN KEY (event_id) references  events(id) ON DELETE CASCADE," +
//                " FOREIGN KEY (user_email) references users(email) ON DELETE CASCADE " +
//                ")";
//        try(Connection connection = DriverManager.getConnection(url,username,password)){
//            PreparedStatement ps = connection.prepareStatement(sql);
//            ps.executeUpdate();
//        } catch (SQLException throwables) {
//            throw new DbException(throwables.getMessage());
//        }
    }

    public void addSubscriber(Integer evId, String userEmail){
        if(existsSubscription(evId, userEmail))
            throw new RepoException("You already subscribed to this event!");
        String sql = "INSERT INTO " + subscriptionsTable + " (event_id, user_email) values (?, ?) ";
        try(Connection connection = DriverManager.getConnection(url, username, password)){
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, evId);
            ps.setString(2, userEmail);
            ps.executeUpdate();
            notifyObservers();
           } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
    }

    private boolean existsSubscription(Integer evId, String userEmail) {
        String sql = "SELECT * FROM  " + subscriptionsTable + " WHERE  event_id = ? AND user_email= ?";
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, evId);
            ps.setString(2, userEmail);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next())
                return true;
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
        return false;

    }

    public void removeSubscriber(Integer evId, String userEmail){
        String sql = "DELETE FROM " + subscriptionsTable + " WHERE event_id = ? AND user_email = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, evId);
            ps.setString(2, userEmail);
            ps.executeUpdate();
            notifyObservers();
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
    }

    public List<Integer> getEventsForUser(String userEmail) {
        List<Integer> eventsId = new ArrayList<>();
        String sql = "SELECT event_id FROM "+ subscriptionsTable + " WHERE user_email = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, userEmail);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next())
                eventsId.add(resultSet.getInt("event_id"));
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
        return eventsId;
    }

    public List<Integer> getUserEventsPage(String email, int firstrow, int rowcount) {
        List<Integer> eventsIds = new ArrayList<>();
        String sql = "SELECT event_id FROM " + subscriptionsTable + " WHERE user_email = ?" +
                " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, firstrow);
            ps.setInt(3, rowcount);
            ResultSet res = ps.executeQuery();
            while (res.next())
                eventsIds.add(res.getInt("event_id"));
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return eventsIds;
    }

    public List<Integer> getFilteredUserEventsPage(String email, int firsrow, int rowcount, String pattern) {
        List<Integer> eventsIds = new ArrayList<>();
        String sql = "SELECT event_id FROM (SELECT event_id FROM " + subscriptionsTable + " WHERE user_email = ?) sb" +
                " INNER JOIN " + eventsTable + " ON event_id = id" +
                " WHERE LOWER(name) LIKE ?" +
                " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, pattern + "%");
            ps.setInt(3, firsrow);
            ps.setInt(4, rowcount);
            ResultSet res = ps.executeQuery();
            while (res.next())
                eventsIds.add(res.getInt("event_id"));
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return eventsIds;
    }

    public int getFilteredUserEventsSize(String email, String pattern) {
        String sql = "SELECT COUNT(*) AS size FROM" +
                " (SELECT event_id FROM " + subscriptionsTable + " WHERE user_email = ?) sb" +
                " INNER JOIN " + eventsTable + " ON event_id = id" +
                " WHERE LOWER(name) LIKE ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, pattern + "%");
            ResultSet res = ps.executeQuery();
            if (res.next())
                return res.getInt("size");
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return 0;
    }

    /**
     * Return the number of events that user are subscribed to it
     * @param email
     * @return
     */
    public int getUserEventsSize(String email) {
        String sql = "SELECT COUNT(*) AS size FROM " + subscriptionsTable +
                " WHERE user_email = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet res = ps.executeQuery();
            if (res.next())
                return res.getInt("size");
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return 0;
    }

    public boolean isSubscribed(String email, Integer id) {
        String sql = "SELECT * FROM " + subscriptionsTable +
                " WHERE user_email = ? AND event_id = ? ";
        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, id);
            ResultSet res = ps.executeQuery();
            if (res.next())
                return true;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return false;
    }
}
