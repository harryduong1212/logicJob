package com.demo.LogicJob.Service;

import com.demo.LogicJob.DAO.JobLogicRepository;
import com.demo.LogicJob.DAO.TaskJobRepository;
import com.demo.LogicJob.DAO.UserRepository;
import com.demo.LogicJob.Entity.AppUser;
import com.demo.LogicJob.Entity.JobLogic;
import com.demo.LogicJob.Entity.TaskJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class JobLogicService {

    @Autowired
    private JobLogicRepository jobLogicRepository;

    @Autowired
    private TaskJobRepository taskJobRepository;

    @Autowired
    private UserRepository userRepository;


    public String createNewJob(String jobName, boolean jobFlow, String jobWorker, String jobChecker, Principal principal) {
        try {
            UserDetails loginedUser = (UserDetails) ((Authentication) principal).getPrincipal();

            if( !loginedUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) ) {
                return "You are not authorized to do this";
            }

            JobLogic jobLogic = new JobLogic();
            jobLogic.setJobFlow(jobFlow);
            jobLogic.setJobName(jobName);
            jobLogic.setJobWorker(userRepository.findAppUserByUserName(jobWorker).getUserId());
            if (jobFlow) {
                jobLogic.setJobChecker(userRepository.findAppUserByUserName(jobChecker).getUserId());
            }
            jobLogic.setJobStatus("New");
            jobLogic.setJobValue(0);
            jobLogicRepository.save(jobLogic);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "Success";
    }

    public String createNewTask(Long jobId, String taskName, Principal principal) {
        try {
            UserDetails loginedUser = (UserDetails) ((Authentication) principal).getPrincipal();

            if(!loginedUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                    || !loginedUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MANAGER")) ) {
                return "You are not authorized to do this";
            }

            JobLogic jobLogic = jobLogicRepository.findJobLogicByJobId(jobId);
            if(jobLogic.getJobStatus().equals("Done")) {
                return "This job have been done, please try another";
            }

            TaskJob taskJob = new TaskJob();
            taskJob.setJobTask(jobLogic);
            taskJob.setTaskName(taskName);
            taskJob.setTaskChecker(jobLogic.getJobChecker());
            taskJob.setTaskWorker(jobLogic.getJobWorker());
            taskJob.setTaskStatus("Pending");
            taskJobRepository.save(taskJob);

            if(jobLogic.getJobStatus().equals("New")) {
                jobLogic.setJobStatus("Inprogress");
                jobLogicRepository.save(jobLogic);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "Success";
    }

    public String userWorking(Long taskId, int taskValue, Principal principal) {
        try {
            UserDetails loginedUser = (UserDetails) ((Authentication) principal).getPrincipal();

            TaskJob taskJob = taskJobRepository.findByTaskId(taskId);

            if( ( !loginedUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                    || !loginedUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MANAGER")) )
                    && !userRepository.findAppUserByUserName(principal.getName()).getUserId()
                    .equals(taskJob.getTaskWorker()) ) {
                return "You are not authorized to do this";
            }

            if(!taskJob.getTaskStatus().equals("Pending") || !taskJob.getTaskStatus().equals("Remanded")) {
                return "This task have been done, please try another";
            }

            taskJob.setTaskValue(taskValue);
            taskJob.setTaskStatus("Confirmed");
            taskJobRepository.save(taskJob);

            JobLogic jobLogic = taskJob.getJobTask();
            if(!taskJob.getJobTask().isJobFlow()) {
                if(taskJobRepository.findByTaskStatusAndJobTask("Pending", jobLogic).isEmpty()) {
                    jobLogic.setJobStatus("Done");
                    jobLogicRepository.save(jobLogic);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "Success";
    }

    public String userChecking(Long taskId, boolean checkResult, Principal principal) {
        try {
            UserDetails loginedUser = (UserDetails) ((Authentication) principal).getPrincipal();

            if(loginedUser.getAuthorities().isEmpty()) {
                return "Please login first";
            }

            TaskJob taskJob = taskJobRepository.findByTaskId(taskId);

            if( ( !loginedUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                    || !loginedUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MANAGER")) )
                    && !userRepository.findAppUserByUserName(principal.getName()).getUserId()
                    .equals(taskJob.getTaskChecker()) ) {
                return "You are not authorized to do this";
            }

            if(checkResult) {
                taskJob.setTaskStatus("Checked");
            } else {
                taskJob.setTaskStatus("Remanded");
            }
            JobLogic jobLogic = taskJob.getJobTask();

            taskJobRepository.save(taskJob);

            if(taskJobRepository.findByTaskStatusAndJobTask("Pending", jobLogic).isEmpty()
                    && taskJobRepository.findByTaskStatusAndJobTask("Remanded", jobLogic).isEmpty() ) {
                jobLogic.setJobStatus("Done");
                jobLogicRepository.save(jobLogic);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "Success";
    }
}
