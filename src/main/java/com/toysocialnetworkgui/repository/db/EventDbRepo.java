package com.toysocialnetworkgui.repository.db;


import com.toysocialnetworkgui.domain.Event;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EventDbRepo {
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
                " date_start date NOT NULL, " +
                " date_end date NOT NULL " +
                ")";
        try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password)){
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    public void save(Event event) {
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
}
