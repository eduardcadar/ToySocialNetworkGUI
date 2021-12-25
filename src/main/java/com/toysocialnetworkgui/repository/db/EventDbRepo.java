package com.toysocialnetworkgui.repository.db;


import com.toysocialnetworkgui.domain.Event;
import com.toysocialnetworkgui.repository.EventRepository;
import com.toysocialnetworkgui.repository.RepoException;

import java.sql.*;
import java.util.List;

public class EventDbRepo implements EventRepository {
    private final String url, username, password, eventTable;

    public EventDbRepo(String url, String username, String password, String eventTable){
        this.url = url;
        this.username = username;
        this.password = password;
        this.eventTable = eventTable;
        String sql = "CREATE TABLE IF NOT EXISTS " + eventTable +
                "( id SERIAL PRIMARY KEY, " +
                "name varchar NOT NULL, " +
                " description varchar NOT NULL, " +
                " location varchar NOT NULL, " +
                " date_start varchar NOT NULL, " +
                " date_end varchar NOT NULL " +
                ")";
        try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password)){
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    public void save(Event event) {
        // TODO
        //  - validate event
        //  - check for duplicates ?
        String sql = "INSERT INTO " + eventTable + " (name , description, location , date_start, date_end) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password)){
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1,event.getName());
            ps.setString(2, event.getDescription());
            ps.setString(3, event.getLocation());
            ps.setString(4, event.getStart().toString());
            ps.setString(5, event.getEnd().toString());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }

    }

    @Override
    public Event getEvent(String name) throws RepoException {
        return null;
    }

    @Override
    public void remove(String name) throws RepoException {

    }

    @Override
    public int size() {
        String sql = "SELECT COUNT(*) FROM " + eventTable;
        try(Connection connection = DriverManager.getConnection(url,username, password)){
            PreparedStatement ps =  connection.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0;
    }

    public void clear() {
        String sql = "DELETE FROM " + eventTable;

        try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public List<Event> getAll() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void update(Event event) {

    }
}
