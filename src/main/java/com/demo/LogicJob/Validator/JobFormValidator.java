package com.demo.LogicJob.Validator;

import com.demo.LogicJob.DAO.JobLogicRepository;
import com.demo.LogicJob.DAO.UserRepository;
import com.demo.LogicJob.Entity.JobLogic;
import com.demo.LogicJob.FormDTO.AppUserForm;
import com.demo.LogicJob.FormDTO.JobForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class JobFormValidator implements Validator {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobLogicRepository jobLogicRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass == JobForm.class;
    }

    @Override
    public void validate(Object o, Errors errors) {
        JobForm jobForm = (JobForm) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jobName", "",
                "JobName is required");
        if(jobLogicRepository.findJobLogicByJobName(jobForm.getJobName()) != null) {
            errors.rejectValue("jobName", "Please try different job name, object: ");
        }

        if(jobForm.isJobFlow()) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jobChecker", "",
                    "Please enter checker and worker");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jobWorker", "",
                    "Please enter checker and worker");
            if(jobForm.getJobChecker().compareTo(jobForm.getJobWorker()) == 0) {
                errors.rejectValue("jobChecker", "Worker and checker cannot be duplicated, object: ");
            }
            if(userRepository.findAppUserByUserName(jobForm.getJobChecker()) == null && !jobForm.getJobChecker().equals("")) {
                errors.rejectValue("jobChecker", "Checker you provided does not exist, object: ");
            }
        } else {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jobWorker", "",
                    "Please enter a checker or worker");
            if(!jobForm.getJobChecker().equals("")) {
                errors.rejectValue("jobChecker", "This is job flow for one person, object: ");
                jobForm.setJobChecker("");
            }
        }

        if(userRepository.findAppUserByUserName(jobForm.getJobWorker()) == null && !jobForm.getJobWorker().equals("")) {
            errors.rejectValue("jobWorker", "Worker you provided does not exist, object: ");
        }

        if (errors.hasErrors()) {
            return;
        }
    }
}
