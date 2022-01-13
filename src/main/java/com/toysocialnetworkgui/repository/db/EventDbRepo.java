package com.toysocialnetworkgui.repository.db;


import com.toysocialnetworkgui.domain.Event;
import com.toysocialnetworkgui.repository.EventRepository;
import com.toysocialnetworkgui.repository.RepoException;
import com.toysocialnetworkgui.validator.EventValidator;
import com.toysocialnetworkgui.validator.Validator;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EventDbRepo implements EventRepository {
    private final String url, username, password, eventTable;
    private final Validator<Event> validator = new EventValidator();

    public EventDbRepo(String url, String username, String password, String eventTable){
        this.url = url;
        this.username = username;
        this.password = password;
        this.eventTable = eventTable;
        String sql = "CREATE TABLE IF NOT EXISTS " + eventTable +
                "( id serial, "+
                " name varchar NOT NULL, " +
                " creator varchar NOT NULL, " +
                " description varchar NOT NULL, " +
                " category varchar NOT NULL, " +
                " location varchar NOT NULL, " +
                " date_start varchar NOT NULL, " +
                " date_end varchar NOT NULL, " +
                " PRIMARY KEY(id)" +
                ")";
        String updateTableAddPhoto = "ALTER TABLE " + eventTable +
                " ADD COLUMN IF NOT EXISTS photo_path varchar DEFAULT '/events/error.png'";


        try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password)){
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.executeUpdate();
            PreparedStatement updateStatementAddProfile = connection.prepareStatement(updateTableAddPhoto);
            updateStatementAddProfile.executeUpdate();

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    /**
     * Adds a new event
     * @throws RepoException - if the exists another event with the same name already saved
     * @param event - Event
     */
    public void save(Event event) {
        validator.validate(event);
        if(getEvent(event.getName()) != null){
            throw new RepoException("There is another event with the same name. Check it out if you want to participate there!");
        }
        String sql = "INSERT INTO " + eventTable + " (name,creator, location, category, description, date_start, date_end,photo_path) VALUES (?, ?, ?, ?, ?,?,?,?)";
        try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password)){
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1,event.getName());
            ps.setString(2,event.getOrganizer());
            ps.setString(3, event.getLocation());
            ps.setString(4, event.getCategory());
            ps.setString(5, event.getDescription());
            ps.setString(6, event.getStart().toString());
            ps.setString(7, event.getEnd().toString());
            ps.setString(8, event.getPhotoPath());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }

    }

    @Override
    public Event getEvent(Integer id) {
        String sql = "SELECT * FROM " + eventTable + " WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet resultSet = ps.executeQuery();
            if (!resultSet.next())
                return null;
            String creator = resultSet.getString("creator");
            String nameEv = resultSet.getString("name");
            String description = resultSet.getString("description");
            String location = resultSet.getString("location");
            String category = resultSet.getString("category");
            LocalDate startDate = LocalDate.parse(resultSet.getString("date_start"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate endDate = LocalDate.parse(resultSet.getString("date_end"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String photoPath = resultSet.getString("photo_path");
            return new Event(id, nameEv, creator, location, category, description, startDate, endDate,photoPath);
        }catch (SQLException e){
            throw new DbException(e.getMessage());
        }

    }

    /**
     * Gets an event by its name, (for testing purposes);
     * @param name
     * @return
     */
    @Override
    public Event getEvent(String name) {
        String sql = "SELECT * FROM " + eventTable + " WHERE name = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, name);
            ResultSet resultSet = ps.executeQuery();
            if (!resultSet.next())
                return null;
            Integer id = resultSet.getInt("id");
            String creator = resultSet.getString("creator");
            String nameEv = resultSet.getString("name");
            String description = resultSet.getString("description");
            String location = resultSet.getString("location");
            String category = resultSet.getString("category");
            LocalDate startDate = LocalDate.parse(resultSet.getString("date_start"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate endDate = LocalDate.parse(resultSet.getString("date_end"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String photoPath = resultSet.getString("photo_path");
            return new Event(id, nameEv, creator, location, category, description, startDate, endDate, photoPath);
        }catch (SQLException e){
            throw new DbException(e.getMessage());
        }

    }
    @Override
    public void remove(String name) throws RepoException {
        // TODO
        //  - you can remove an event if its start date is before localDate.now() ??s
        String sql = "DELETE FROM " + eventTable + " WHERE name = ?";
        try(Connection connection = DriverManager.getConnection(url,username,password)){
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, name);
            ps.executeUpdate();

        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
    }

    @Override
    public int size() {
        String sql = "SELECT COUNT(*) as size FROM " + eventTable;
        try (Connection connection = DriverManager.getConnection(url,username, password)) {
            PreparedStatement ps =  connection.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            resultSet.next();
            if (resultSet.next())
                return resultSet.getInt("size");
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
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
    public List<Event> getAll()  {
        ArrayList<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM " + eventTable;
        try(Connection connection = DriverManager.getConnection(url,username,password)) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()){
                Integer id = resultSet.getInt("id");
                String creator = resultSet.getString("creator");
                String nameEv = resultSet.getString("name");
                String description = resultSet.getString("description");
                String location = resultSet.getString("location");
                String category = resultSet.getString("category");
                LocalDate startDate = LocalDate.parse(resultSet.getString("date_start"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                LocalDate endDate = LocalDate.parse(resultSet.getString("date_end"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String photoPath = resultSet.getString("photo_path");
                events.add( new Event(id, nameEv, creator, location, category, description, startDate, endDate,photoPath));
          }
        }catch (SQLException e){
            throw new DbException(e.getMessage());
        }
        return events;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public void update(Event event)  {
        // TODO LATER
        //  - if you want to update dont ask for all fields, in this case we can rework the save method
        //   as it would update fields
    }

    /**
     * Returns a page with events
     * @param firstrow how many events to skip
     * @param rowcount how many events to return
     * @return list of events
     */
    public List<Event> getEventsPage(int firstrow, int rowcount) {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM " + eventTable +
                " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, firstrow);
            ps.setInt(2, rowcount);
            ResultSet res = ps.executeQuery();
            while (res.next()) {
                Integer id = res.getInt("id");
                String creator = res.getString("creator");
                String name = res.getString("name");
                String description = res.getString("description");
                String location = res.getString("location");
                String category = res.getString("category");
                LocalDate startDate = LocalDate.parse(res.getString("date_start"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                LocalDate endDate = LocalDate.parse(res.getString("date_end"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String photoPath = res.getString("photo_path");
                events.add(new Event(id, name, creator, location, category, description, startDate, endDate,photoPath));
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return events;
    }
}
