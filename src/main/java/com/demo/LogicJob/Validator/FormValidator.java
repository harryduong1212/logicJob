package com.demo.LogicJob.Validator;

import com.demo.LogicJob.DAO.UserRepository;
import com.demo.LogicJob.Entity.AppUser;
import com.demo.LogicJob.FormDTO.AppUserForm;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class FormValidator implements Validator {

    // common-validator library.
    private EmailValidator emailValidator = EmailValidator.getInstance();

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass == AppUserForm.class;
    }

    @Override
    public void validate(Object o, Errors errors) {
        AppUserForm form = (AppUserForm) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "", "Email is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userName", "", "User name is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "", "First name is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "", "Last name is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "", "Password is required");

        if (errors.hasErrors()) {
            return;
        }

        if (!emailValidator.isValid(form.getEmail())) {

            errors.rejectValue("email", "", "Email is not valid");
            return;
        }

        AppUser userAccount = userRepository.findAppUserByUserName( form.getUserName());
        if (userAccount != null) {
            if (form.getUserId() == null) {
                errors.rejectValue("userName", "", "User name is not available");
                return;
            } else if (!form.getUserId().equals( userAccount.getUserId() )) {
                errors.rejectValue("userName", "", "User name is not available");
                return;
            }
        }

        userAccount = userRepository.findAppUserByEmail(form.getEmail());
        if (userAccount != null) {
            if (form.getUserId() == null) {
                errors.rejectValue("email", "", "Email is not available");
                return;
            } else if (!form.getUserId().equals( userAccount.getUserId() )) {
                errors.rejectValue("email", "", "Email is not available");
                return;
            }
        }
    }
}
