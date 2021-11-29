package com.toysocialnetworkgui.validator;

public interface Validator<E> {
    void validate(E e) throws ValidatorException;
}
