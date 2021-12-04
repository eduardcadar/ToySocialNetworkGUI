package com.toysocialnetworkgui.validator;

import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.utils.PasswordEncryptor;

import java.util.regex.Pattern;

public class UserValidator implements Validator<User> {
    private final Pattern namePattern = Pattern.compile("^[a-zA-Z\s]+$");
    private final Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9._+-]+@[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+){1,2}$");

    /**
     * Validates a user
     * @param user - the user to be validated
     * @throws ValidatorException - if firstname/lastname are empty fields or the email is not valid
     */
    public void validate(User user) throws ValidatorException {
        if (!namePattern.matcher(user.getLastName()).matches()) throw new ValidatorException("Invalid last name");
        if (!namePattern.matcher(user.getFirstName()).matches()) throw new ValidatorException("Invalid first name");
        if (!emailPattern.matcher(user.getEmail()).matches()) throw new ValidatorException("Invalid email");
        if (user.getPassword().equals(PasswordEncryptor.toHexString(PasswordEncryptor.getSHA(""))))
            throw new ValidatorException("Invalid password");
    }
}
