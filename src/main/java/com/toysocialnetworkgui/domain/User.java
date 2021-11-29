package com.toysocialnetworkgui.domain;

import com.toysocialnetworkgui.utils.PasswordEncryptor;

import java.util.*;

public class User {
    private String firstName, lastName, email, password;

    /**
     * Creates an User object, with the attributes given as parameters
     * @param firstName - String
     * @param lastName - String
     * @param email - String
     * @param password - String before encrypt
     */
    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        if (password.length() == 64) this.password = password;
        else this.password = PasswordEncryptor.toHexString(PasswordEncryptor.getSHA(password));
    }

    /**
     * Creates a dummy user with a default password
     * @param firstName - String
     * @param lastName - String
     * @param email - String
     */
    public User(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = PasswordEncryptor.toHexString(PasswordEncryptor.getSHA("000000"));
    }

    /**
     * Returns the first name of an user
     * @return firstName - String
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Returns the last name of an user
     * @return lastName - String
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Updates the user's first name and last name and returns the user
     * @param firstname - the new first name
     * @param lastname - the new last name
     * @param password - the new password
     * @return User
     */
    public User update(String firstname, String lastname, String password) {
        this.firstName = firstname;
        this.lastName = lastname;
        setPassword(password);
        return this;
    }

    /**
     * Updates the user's first name and last name and returns the user
     * @param firstname - the new first name
     * @param lastname - the new last name
     * @return User
     */
    public User update(String firstname, String lastname) {
        this.firstName = firstname;
        this.lastName = lastname;
        return this;
    }

    @Override
    public String toString() {
        return lastName + " " + firstName;
    }

    /**
     * Returns the email of a user
     * @return email - String
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the password of a user
     * @return password -String
     */
    public String getPassword(){
        return password;
    }

    /**
     *
     * Sets the new password for the user
     */
    public void setPassword(String password){
        this.password = PasswordEncryptor.toHexString(PasswordEncryptor.getSHA(password));

    }
    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    /**
     * Verifies if two users have the same email
     * @param o - Object
     * @return true if they have the same email, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User that = (User) o;
        return Objects.equals(email, that.email);
    }
}
