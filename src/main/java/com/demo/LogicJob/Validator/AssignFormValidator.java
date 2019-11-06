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
public class AssignFormValidator implements Validator {

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

        if(jobForm.getJobChecker().equals("") && jobForm.getJobWorker().equals("")) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jobChecker", "", "Please enter a checker or worker");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jobWorker", "", "Please enter a checker or worker");

        }
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jobId", "", "JobId is required");


        JobLogic tempJob = jobLogicRepository.findJobLogicByJobId(jobForm.getJobId());
        if(tempJob == null) {
            errors.rejectValue("jobId", "Can not found job by given jobId, object: ");
        }

        if(jobForm.getJobChecker() != null && !jobForm.getJobChecker().equals("")) {
            if(jobForm.getJobChecker().compareTo("") != 0 && tempJob != null && tempJob.getJobChecker() != null) {
                if(userRepository.findAppUserByUserId(tempJob.getJobChecker()).getUserName()
                        .compareTo(jobForm.getJobChecker()) != 0) {
                    errors.rejectValue("jobChecker",
                            "Your given checker does not match the current checker, object: ");
                }
            }
        }

        if(jobForm.getJobWorker() != null && !jobForm.getJobWorker().equals("")) {
            if(jobForm.getJobWorker().compareTo("") != 0 && tempJob != null && tempJob.getJobWorker() != null) {
                if(userRepository.findAppUserByUserId(tempJob.getJobWorker()).getUserName()
                        .compareTo(jobForm.getJobWorker()) != 0) {
                    errors.rejectValue("jobWorker",
                            "Your given worker does not match the current worker, object: ");
                }
            }
        }




        if (errors.hasErrors()) {
            return;
        }
    }
}
