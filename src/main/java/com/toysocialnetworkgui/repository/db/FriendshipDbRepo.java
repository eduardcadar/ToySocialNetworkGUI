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

public class FriendshipDbRepo implements Observable, FriendshipRepository {
    private final String url;
    private final String username;
    private final String password;
    private final String fshipsTable;
    private final String usersTable;
    private final Validator<Friendship> val;

    public FriendshipDbRepo(String url, String username, String password, Validator<Friendship> val, String fshipsTable, String usersTable) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.val = val;
        this.fshipsTable = fshipsTable;
        this.usersTable = usersTable;

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
            notifyObservers();
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
            notifyObservers();
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
            if (res.next())
                return res.getInt("size");
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return 0;
    }

    /**
     * @param email the email of the user
     * @return number of friends of a user
     */
    public int getUserFriendsSize(String email) {
        String sql = "SELECT COUNT(*) AS size FROM " + fshipsTable +
                "WHERE email1 = ? OR email2 = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet res = ps.executeQuery();
            if (res.next())
                return res.getInt("size");
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return 0;
    }

    /**
     * @param email the email of the user
     * @param pattern string that friends of the user should have in their names
     * @return number of friends that contain the pattern in their names
     */
    public int getUserFriendsFilteredSize(String email, String pattern) {
        String sql = "SELECT COUNT(*) AS size FROM " +
                " (SELECT email1, email2 FROM " + fshipsTable +
                " WHERE email1 = ? OR email2 = ?) fr" +
                " INNER JOIN " + usersTable +
                " ON ((email = email1 AND email1 <> ?) OR (email = email2 AND email2 <> ?))" +
                " WHERE LOWER(firstname) LIKE ? OR LOWER(lastname) LIKE ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, email);
            ps.setString(3, email);
            ps.setString(4, email);
            ps.setString(5, pattern + "%");
            ps.setString(6, pattern + "%");
            ResultSet res = ps.executeQuery();
            if (res.next())
                return res.getInt("size");
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return 0;
    }

    /**
     * @param email the email of the user
     * @param pattern string that friends should have in their names
     * @param month friendships' month
     * @return number of friends the user made in the specified month, that have the pattern in their names
     */
    public int getUserFriendsMonthFilteredSize(String email, String pattern, int month) {
        String monthStr = String.valueOf(month);
        if (month < 10) monthStr = "0" + monthStr;
        String sql = "SELECT COUNT(*) AS size FROM " +
                " (SELECT email1, email2 FROM " + fshipsTable +
                " WHERE (email1 = ? OR email2 = ?) AND date LIKE ?) fr" +
                " INNER JOIN " + usersTable +
                " ON ((email = email1 AND email1 <> ?) OR (email = email2 AND email2 <> ?))" +
                " WHERE LOWER(firstname) LIKE ? OR LOWER(lastname) LIKE ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, email);
            ps.setString(3, "%-" + monthStr + "-%");
            ps.setString(4, email);
            ps.setString(5, email);
            ps.setString(6, pattern + "%");
            ps.setString(7, pattern + "%");
            ResultSet res = ps.executeQuery();
            if (res.next())
                return res.getInt("size");
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
            notifyObservers();
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
                String date = res.getString("date");// 2021-12-15
                String[] args = date.split("-");
                int year = Integer.parseInt(args[0]);
                int month = Integer.parseInt(args[1]);
                int day = Integer.parseInt(args[2]);
                LocalDate localDate = LocalDate.of(year,month,day);
                fships.add(new Friendship(email1, email2,localDate));
            }
            return fships;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    /**
     * Returns a list with the emails of the user's friends that contain 'pattern' in their names,
     * skipping the first 'firstrow' ones and returning the next 'rowcount' ones
     * @param email email of the user
     * @param firstrow how many results to ignore
     * @param rowcount how many results to return
     * @param pattern what should the name of a user contain
     * @return list with emails of the friends requested
     */
    public List<String> getUserFriendsFilteredPage(String email, int firstrow, int rowcount, String pattern) {
        List<String> friends = new ArrayList<>();
        String sql = "SELECT email1, email2 FROM" +
                " (SELECT email1, email2 FROM " + fshipsTable +
                " WHERE email1 = ? OR email2 = ?) fr" +
                " INNER JOIN " + usersTable +
                " ON ((email = email1 AND email1 <> ?) OR (email = email2 AND email2 <> ?))" +
                " WHERE LOWER(firstname) LIKE ? OR LOWER(lastname) LIKE ?" +
                " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, email);
            ps.setString(3, email);
            ps.setString(4, email);
            ps.setString(5, pattern + "%");
            ps.setString(6, pattern + "%");
            ps.setInt(7, firstrow);
            ps.setInt(8, rowcount);
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
     * Returns a list with the emails of the user's friends that contain 'pattern' in their names,
     * have become friends in the month specified,
     * skipping the first 'firstrow' ones and returning the next 'rowcount' ones
     * @param email email of the user
     * @param firstrow how many results to ignore
     * @param rowcount how many results to return
     * @param pattern what should the name of a user contain
     * @param month the month in which the user became friends with the other users
     * @return list with emails of the friends requested
     */
    public List<String> getUserFriendsMonthFilteredPage(String email, int firstrow, int rowcount, String pattern, int month) {
        List<String> friends = new ArrayList<>();
        String monthStr = String.valueOf(month);
        if (month < 10) monthStr = "0" + monthStr;
        String sql = "SELECT email1, email2 FROM " +
                " (SELECT email1, email2 FROM " + fshipsTable +
                " WHERE (email1 = ? OR email2 = ?) AND date LIKE ?) fr" +
                " INNER JOIN " + usersTable +
                " ON ((email = email1 AND email1 <> ?) OR (email = email2 AND email2 <> ?))" +
                " WHERE LOWER(firstname) LIKE ? OR LOWER(lastname) LIKE ?" +
                " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, email);
            ps.setString(3, "%-" + monthStr + "-%");
            ps.setString(4, email);
            ps.setString(5, email);
            ps.setString(6, pattern + "%");
            ps.setString(7, pattern + "%");
            ps.setInt(8, firstrow);
            ps.setInt(9, rowcount);
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
            notifyObservers();
        } catch (SQLException throwables) {
            throw new DbException(throwables.getMessage());
        }
    }
}
