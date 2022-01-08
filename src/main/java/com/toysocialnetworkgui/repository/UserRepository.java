package com.toysocialnetworkgui.repository;

import com.toysocialnetworkgui.domain.User;

import java.util.List;

public interface UserRepository {
    void save(User u) throws RepoException;
    User getUser(String email) throws RepoException;
    void remove(String email) throws RepoException;
    int size();
    void clear();
    List<User> getAll();
    boolean isEmpty();
    User update(User user);
}
