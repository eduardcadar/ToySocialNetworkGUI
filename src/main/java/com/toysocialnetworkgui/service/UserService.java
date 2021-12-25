package com.toysocialnetworkgui.service;

import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.repository.UserRepository;
import com.toysocialnetworkgui.validator.ValidatorException;

import java.util.List;

public class UserService {
    UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    /**
     * Adds a user to the repository
     * @param firstname - the new first name of the user
     * @param lastname - the new last name of the user
     * @param email - the email of the user to be updated
     * @param password - the new password of the user
     */
    public void save(String firstname, String lastname, String email, String password) {
        if(firstname.equals("") || lastname.equals("") || email.equals("") || password.equals(""))
            throw new ValidatorException("Field can't be empty");
        if(password.length() <= 5)
            throw new ValidatorException("The password must contain at least 6 characters");
        repo.save(new User(firstname, lastname, email, password));
    }

    /**
     * Removes a user from the repository
     * @param email - String the email of the user to be removed
     */
    public void remove(String email) {
        repo.remove(email);
    }

    /**
     * Updates a user in the repository
     * @param firstname - the new first name of the user
     * @param lastname - the new last name of the user
     * @param email - the email of the user to be updated
     * @param password - the new password of the user
     */
    public void updateUser(String firstname, String lastname, String email, String password) {
        repo.update(new User(firstname, lastname, email, password));
    }

    /**
     * @param email - String with the email of the user to be returned
     * @return the user with the email given as a parameter,
     * null if no user in the repository has the given email
     */
    public User getUser(String email) {
        return repo.getUser(email);
    }

    /**
     * @return all the users saved in the repository
     */
    public List<User> getAllUsers() {
        return repo.getAll();
    }

    /**
     * @return true if the repository has no users saved, false otherwise
     */
    public boolean isEmpty() {
        return repo.isEmpty();
    }

    /**
     * @return the number of users saved in the repository
     */
    public int size() {
        return repo.size();
    }
}
