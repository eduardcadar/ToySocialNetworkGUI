package com.toysocialnetworkgui.repository.db;

import com.toysocialnetworkgui.domain.Friendship;
import com.toysocialnetworkgui.repository.FriendshipRepository;
import com.toysocialnetworkgui.repository.RepoException;
import com.toysocialnetworkgui.repository.observer.Observable;
import com.toysocialnetworkgui.validator.Validator;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FriendshipDbRepo extends Observable implements FriendshipRepository {
    private final String url;
    private final String username;
    private final String password;
    private final String fshipsTable;
    private final Validator<Friendship> val;

    public FriendshipDbRepo(String url, String username, String password, Validator<Friendship> val, String fshipsTable) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.val = val;
        this.fshipsTable = fshipsTable;

        String sql = "CREATE TABLE IF NOT EXISTS " + fshipsTable +
                "(email1 varchar," +
                " email2 varchar, " +
                " date varchar DEFAULT NULL," +
                " PRIMARY KEY (email1,email2)," +
                " FOREIGN KEY (email1) references users(email) ON DELETE CASCADE," +
                " FOREIGN KEY (email2) references users(email) ON DELETE CASCADE" +
                ")";


        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
    }

    /**
     * Validates and adds a friendship to the database
     * @param f - the friendship to be added
     */
    @Override
    public void addFriendship(Friendship f) {
        val.validate(f);
        if (getFriendship(f.getFirst(), f.getSecond()) != null)
            throw new RepoException("These two users are already friends");
        String sql = "INSERT INTO " + fshipsTable + " (email1, email2, date) VALUES (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, f.getFirst());
            ps.setString(2, f.getSecond());
            ps.setString(3, f.getDate().toString());
            ps.executeUpdate();
            super.notifyObservers();
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
    }

    /**
     * @param email1 - String the email of the first user
     * @param email2 - String the email of the second user
     * @return the friendship of the two users if it is saved in the database,
     * null otherwise
     */
    public Friendship getFriendship(String email1, String email2) {
        String sql = "SELECT * FROM " + fshipsTable + " WHERE (email1 = ? AND email2 = ?) OR (email2 = ? AND email1 = ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email1);
            ps.setString(2, email2);
            ps.setString(3, email1);
            ps.setString(4, email2);
            ResultSet res = ps.executeQuery();
            if (!res.next())
                return null;
            Friendship friendship =  new Friendship(res.getString("email1"), res.getString("email2"));
            String date =  res.getString("date");
            if (date != null)
                friendship.setDate(LocalDate.parse(date));
            else
             friendship.setDate(null);
            return friendship;

        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
    }

    /**
     * Removes a friendship from the database
     * @param f - the friendship to be removed
     */
    @Override
    public void removeFriendship(Friendship f) {
        if (getFriendship(f.getFirst(), f.getSecond()) == null)
            throw new RepoException("These two users aren't friends");
        String sql = "DELETE FROM " + fshipsTable + " WHERE (email1 = ? AND email2 = ?) OR (email2 = ? AND email1 = ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, f.getFirst());
            ps.setString(2, f.getSecond());
            ps.setString(3, f.getFirst());
            ps.setString(4, f.getSecond());
            ps.executeUpdate();
            super.notifyObservers();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    /**
     * @return the number of friendships saved in the database
     */
    @Override
    public int size() {
        String sql = "SELECT COUNT(*) AS size FROM " + fshipsTable;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet res = ps.executeQuery();
            if (res.next()) {
                return res.getInt("size");
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return 0;
    }

    /**
     * Removes all friendships from the database
     */
    @Override
    public void clear() {
        String sql = "DELETE FROM " + fshipsTable;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.executeUpdate();
            super.notifyObservers();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    /**
     * @return true if there are no friendships saved, false otherwise
     */
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }


    /** Returns all the friendships saved in the database
     *  @return List<Friendship>
     */
    public List<Friendship> getAll() {
        List<Friendship> fships = new ArrayList<>();
        String sql = "SELECT * FROM " + fshipsTable;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet res = ps.executeQuery();
            while (res.next()) {
                String email1 = res.getString("email1");
                String email2 = res.getString("email2");
                fships.add(new Friendship(email1, email2));
            }
            return fships;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    public List<String> getUserFriendsPage(String email, int firstrow, int rowcount) {
        List<String> friends = new ArrayList<>();
        String sql = "SELECT * FROM " + fshipsTable + " WHERE email1 = ? OR email2 = ?" +
                " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, email);
            ps.setInt(3, firstrow);
            ps.setInt(4, rowcount);
            ResultSet res = ps.executeQuery();
            while (res.next()) {
                String email1 = res.getString("email1");
                String email2 = res.getString("email2");
                if (email1.equals(email))
                    friends.add(email2);
                else
                    friends.add(email1);
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return friends;
    }

    /**
     * @param email - String the email of the user
     * @return a list with the emails of a user's friends
     */
    @Override
    public List<String> getUserFriends(String email) {
        List<String> friends = new ArrayList<>();
        for (Friendship f : getAll()) {
            if (f.getFirst().equals(email))
                friends.add(f.getSecond());
            else if (f.getSecond().equals(email))
                friends.add(f.getFirst());
        }
        return friends;
    }

    /**
     * Removes the friendships of a user
     * @param email - String the email of the user
     */
    @Override
    public void removeUserFships(String email) {
        String sql = "DELETE FROM " + fshipsTable + " WHERE email1 = ? OR email2 = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, email);
            ps.executeUpdate();
            super.notifyObservers();
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
    }
}
