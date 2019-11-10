package com.demo.LogicJob.Service;

import com.demo.LogicJob.DAO.JobLogicRepository;
import com.demo.LogicJob.DAO.UserMapper;
import com.demo.LogicJob.DAO.UserRepository;
import com.demo.LogicJob.Entity.JobLogic;
import com.demo.LogicJob.Entity.TaskJob;
import com.demo.LogicJob.FormDTO.AppUserForm;
import com.demo.LogicJob.Entity.AppUser;
import com.demo.LogicJob.FormDTO.JobForm;
import com.demo.LogicJob.FormDTO.TaskForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.List;

@Generated(
        value = "org.mapstruct.ap.MappingProcessor"
)
@Component
public class UserMapperImpl implements UserMapper {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private JobLogicRepository jobLogicRepository;

    @Autowired
    private UserRepository userRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserMapperImpl.class);

    // sleep function
    private void simulateSlowService() {
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public JobLogic toJobLogic(JobForm jobForm) {
        if(jobForm == null) { return null; }

        JobLogic.JobLogicBuilder jobLogic = JobLogic.builder();
        if(jobForm.getJobChecker() != null && jobForm.getJobChecker() != "") {
            jobLogic.jobChecker(userRepository.findAppUserByUserName(jobForm.getJobChecker()).getUserId());
        }
        if(jobForm.getJobWorker() != null && jobForm.getJobWorker() != "") {
            jobLogic.jobWorker(userRepository.findAppUserByUserName(jobForm.getJobWorker()).getUserId());
        }
        jobLogic.jobFlow(jobForm.isJobFlow());
        if(jobForm.getJobId() != null) {
            jobLogic.jobId(jobForm.getJobId());
        }
        if(jobForm.getJobName() != null) {
            jobLogic.jobName(jobForm.getJobName());
        }
        if(jobForm.getJobStatus() != null) {
            jobLogic.jobStatus(jobForm.getJobStatus());
        }
        return jobLogic.build();
    }

    @Override
    public JobForm toJobForm(JobLogic jobLogic) {
        if(jobLogic == null) { return null; }

        JobForm.JobFormBuilder jobForm = JobForm.builder();
        if(jobLogic.getJobChecker() != null) {
            jobForm.jobChecker(userRepository.findAppUserByUserId(jobLogic.getJobChecker()).getUserName());
        }
        if(jobLogic.getJobWorker() != null) {
            jobForm.jobWorker(userRepository.findAppUserByUserId(jobLogic.getJobWorker()).getUserName());
        }
        jobForm.jobFlow(jobLogic.isJobFlow());
        if(jobLogic.getJobId() != null) {
            jobForm.jobId(jobLogic.getJobId());
        }
        if(jobLogic.getJobName() != null) {
            jobForm.jobName(jobLogic.getJobName());
        }
        if(jobLogic.getJobStatus() != null) {
            jobForm.jobStatus(jobLogic.getJobStatus());
        }
        return jobForm.build();
    }

    @Override
    public TaskJob toTaskJob(TaskForm taskForm) {
        if(taskForm == null) { return null; }

        TaskJob.TaskJobBuilder taskJob = TaskJob.builder();
        if(taskForm.getJobId() != null) {
            JobLogic jobLogic = jobLogicRepository.findJobLogicByJobId(taskForm.getJobId());
            taskForm.setTaskChecker(jobLogic.getJobChecker());
            taskForm.setTaskWorker(jobLogic.getJobWorker());
            taskJob.jobTask(jobLogic);
        }
        if(taskForm.getTaskChecker() != null ) {
            taskJob.taskChecker(userRepository.findAppUserByUserId(taskForm.getTaskChecker()).getUserId());
        }
        if(taskForm.getTaskWorker() != null ) {
            taskJob.taskWorker(userRepository.findAppUserByUserId(taskForm.getTaskWorker()).getUserId());
        }

        if(taskForm.getTaskId() != null) {
            taskJob.taskId(taskForm.getTaskId());
        }
        if(taskForm.getTaskName() != null) {
            taskJob.taskName(taskForm.getTaskName());
        }
        if(taskForm.getTaskStatus() != null) {
            taskJob.taskStatus(taskForm.getTaskStatus());
        }
        taskJob.taskValue(taskForm.getTaskValue());
        return taskJob.build();
    }

    @Override
    public TaskForm toTaskForm(TaskJob taskJob) {
        if(taskJob == null) { return null; }

        TaskForm.TaskFormBuilder taskForm = TaskForm.builder();
        if(taskJob.getTaskChecker() != null) {
            taskForm.taskChecker(userRepository.findAppUserByUserId(taskJob.getTaskChecker()).getUserId());
        }
        if(taskJob.getTaskWorker() != null) {
            taskForm.taskWorker(userRepository.findAppUserByUserId(taskJob.getTaskWorker()).getUserId());
        }
        if(taskJob.getTaskId() != null) {
            taskForm.taskId(taskJob.getTaskId());
        }
        if(taskJob.getTaskName() != null) {
            taskForm.taskName(taskJob.getTaskName());
        }
        if(taskJob.getTaskStatus() != null) {
            taskForm.taskStatus(taskJob.getTaskStatus());
        }
        taskForm.taskValue(taskJob.getTaskValue());
        return taskForm.build();
    }

    @Override
    public AppUserForm toUserForm(AppUser appUser) {
        if(appUser == null) { return null; }

        AppUserForm.AppUserFormBuilder appUserForm = AppUserForm.builder();

        appUserForm.userName(appUser.getUserName());
        appUserForm.userId(appUser.getUserId());
        appUserForm.email(appUser.getEmail());
        appUserForm.enable(appUser.isEnabled());
        appUserForm.role(appUser.getRoles().getRoleName());
        appUserForm.update(appUser.getLastModifiedDate());
        appUserForm.create(appUser.getCreatedDate());
        appUserForm.firstName(appUser.getFirstName());
        appUserForm.lastName(appUser.getLastName());
        appUserForm.role(appUser.getRoles().getRoleName());
        LOGGER.info("Convert appUser to appUserForm successfully! User: " + appUser.getUserName());
        return appUserForm.build();
    }

    @Override
    public AppUser toAppUser(AppUserForm appUserForm) {
        if(appUserForm == null) { return null; }

        AppUser.AppUserBuilder appUser = AppUser.builder();

        appUser.userName(appUserForm.getUserName());
        appUser.encrytedPassword(bCryptPasswordEncoder.encode(appUserForm.getPassword()));
        appUser.email(appUserForm.getEmail());
        LOGGER.info("Convert appUserForm to appUser successfully! User: " + appUserForm.getUserName());
        return appUser.build();
    }

    @Override
    public List<AppUserForm> toAppUserForm(List<AppUser> appUser) {
        if(appUser == null) { return null; }
        List<AppUserForm> appUserForms = new ArrayList<>();
        for (AppUser user : appUser) {
            AppUserForm.AppUserFormBuilder appUserForm = AppUserForm.builder();
            appUserForm.userName(user.getUserName());
            appUserForm.userId(user.getUserId());
            appUserForm.email(user.getEmail());
            appUserForm.enable(user.isEnabled());
            appUserForm.role(user.getRoles().getRoleName());
            appUserForm.update(user.getLastModifiedDate());
            appUserForm.create(user.getCreatedDate());
            appUserForms.add(appUserForm.build());
        }
        LOGGER.info("Convert appUserList to appUserFormList successfully!");
        return appUserForms;
    }
}
