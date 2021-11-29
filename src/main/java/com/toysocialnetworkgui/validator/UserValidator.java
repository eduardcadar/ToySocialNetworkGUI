package com.toysocialnetworkgui.validator;

import com.toysocialnetworkgui.domain.User;

import java.util.regex.Pattern;

public class UserValidator implements Validator<User> {
    private Pattern namePattern = Pattern.compile("^[a-zA-Z\s]+$");
    private Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9._+-]+@[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+){1,2}$");

    /**
     * Validates a user
     * @param user - the user to be validated
     * @throws ValidatorException - if firstname/lastname are empty fields or the email is not valid
     */
    public void validate(User user) throws ValidatorException {
        if (!namePattern.matcher(user.getLastName()).matches()) throw new ValidatorException("The last name has to only contain letters");
        if (!namePattern.matcher(user.getFirstName()).matches()) throw new ValidatorException("The first name has to only contain letters");
        if (!emailPattern.matcher(user.getEmail()).matches()) throw new ValidatorException("Invalid email");
    }
}
