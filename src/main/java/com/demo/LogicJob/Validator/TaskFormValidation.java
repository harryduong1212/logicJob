package com.demo.LogicJob.Validator;

import com.demo.LogicJob.DAO.JobLogicRepository;
import com.demo.LogicJob.DAO.TaskJobRepository;
import com.demo.LogicJob.DAO.UserRepository;
import com.demo.LogicJob.Entity.JobLogic;
import com.demo.LogicJob.FormDTO.JobForm;
import com.demo.LogicJob.FormDTO.TaskForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class TaskFormValidation implements Validator {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskJobRepository taskJobRepository;

    @Autowired
    private JobLogicRepository jobLogicRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass == TaskForm.class;
    }

    @Override
    public void validate(Object o, Errors errors) {
        TaskForm taskForm = (TaskForm) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jobId", "",
                "jobId is required");
        if(taskForm.getTaskId() == null) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "taskName", "",
                    "taskName is required");
        } else {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "taskId", "",
                    "taskId is required");
        }

        if(taskJobRepository.findByTaskName(taskForm.getTaskName()) != null) {
            errors.rejectValue("taskName", "Please try different task name, object: ");
        }

        JobLogic jobLogic = jobLogicRepository.findJobLogicByJobId(taskForm.getJobId());

        if(jobLogic == null) {
            errors.rejectValue("jobId", "Please enter a valid jobId, object: ");
        }
    }
}
