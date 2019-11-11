package com.demo.LogicJob.Service;

import com.demo.LogicJob.DAO.JobLogicRepository;
import com.demo.LogicJob.DAO.TaskJobRepository;
import com.demo.LogicJob.DAO.UserRepository;
import com.demo.LogicJob.Entity.JobLogic;
import com.demo.LogicJob.Entity.TaskJob;
import com.demo.LogicJob.FormDTO.JobForm;
import com.demo.LogicJob.FormDTO.TaskForm;
import com.demo.LogicJob.Utils.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.Principal;


@Service
@Transactional
public class JobLogicService {

    @Autowired
    private JobLogicRepository jobLogicRepository;

    @Autowired
    private TaskJobRepository taskJobRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapperImpl userMapperImpl;

    private static final Logger LOGGER = LoggerFactory.getLogger(JobLogicService.class);


//    public String createNewJob(String jobName, boolean jobFlow, String jobWorker, String jobChecker, Principal principal) {
    public String createNewJob(JobForm jobForm, Principal principal) {

            try {
            UserDetails loginedUser = (UserDetails) ((Authentication) principal).getPrincipal();

            if( !loginedUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) ) {
                return "You are not authorized to do this";
            }

            JobLogic jobLogic = userMapperImpl.toJobLogic(jobForm);
            jobLogic.setJobStatus("New");

//            JobLogic jobLogic = new JobLogic();
//            jobLogic.setJobFlow(jobFlow);
//            jobLogic.setJobName(jobName);
//            jobLogic.setJobWorker(userRepository.findAppUserByUserName(jobWorker).getUserId());
//            if (jobFlow) {
//                jobLogic.setJobChecker(userRepository.findAppUserByUserName(jobChecker).getUserId());
//            }
//            jobLogic.setJobStatus("New");
//            jobLogic.setJobValue(0);

            jobLogicRepository.save(jobLogic);
            LOGGER.info("JobName " + jobLogic.getJobName() + " was create by " + WebUtils.toString(loginedUser));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "Success";
    }

//    public String createNewTask(Long jobId, String taskName, Principal principal) {
    public String createNewTask(TaskForm taskForm, Principal principal) {
        try {
            UserDetails loginedUser = (UserDetails) ((Authentication) principal).getPrincipal();

            if(!loginedUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                    && !loginedUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MANAGER")) ) {
                return "You are not authorized to do this";
            }

            JobLogic jobLogic = jobLogicRepository.findJobLogicByJobId(taskForm.getJobId());

            TaskJob taskJob = userMapperImpl.toTaskJob(taskForm);
            taskJob.setTaskStatus("Pending");

            taskJobRepository.save(taskJob);
            LOGGER.info("TaskName " + taskJob.getTaskName() + " in jobId: " + taskJob.getJobTask().getJobId()
                    + " was create by " + WebUtils.toString(loginedUser));

            if(jobLogic.getJobStatus().equals("New") || jobLogic.getJobStatus().equals("Done")) {
                jobLogic.setJobStatus("Inprogress");
                jobLogicRepository.save(jobLogic);
                LOGGER.info("Status of JobName " + jobLogic.getJobName() + " was change to Inprogress");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "Success";
    }

//    public String userWorking(Long taskId, int taskValue, Principal principal) {
    public String userWorking(TaskForm taskForm, Principal principal) {
        try {
            UserDetails loginedUser = (UserDetails) ((Authentication) principal).getPrincipal();

            TaskJob taskJob = taskJobRepository.findByTaskId(taskForm.getTaskId());
            if(taskJob != null) {
                if( ( !loginedUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                        || !loginedUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MANAGER")) )
                        && !userRepository.findAppUserByUserName(principal.getName()).getUserId()
                        .equals(taskJob.getTaskWorker()) ) {
                    return "You are not authorized to do this";
                }
            } else {
                return "Task not found";
            }

            if(!taskJob.getTaskStatus().equals("Pending") && !taskJob.getTaskStatus().equals("Remanded")) {
                return "This task have been done, please try another";
            }

            taskJob.setTaskValue(taskForm.getTaskValue());
            taskJob.setTaskStatus("Confirmed");
            taskJobRepository.save(taskJob);
            LOGGER.info("TaskName " + taskJob.getTaskName() + " in jobId: " + taskJob.getJobTask().getJobId()
                    + " was confirmed by " + WebUtils.toString(loginedUser));

            JobLogic jobLogic = taskJob.getJobTask();
            if(!taskJob.getJobTask().isJobFlow()) {
                if(taskJobRepository.findByTaskStatusAndJobTask("Pending", jobLogic).isEmpty()) {
                    jobLogic.setJobStatus("Done");
                    jobLogicRepository.save(jobLogic);
                    LOGGER.info("Status of JobName " + jobLogic.getJobName() + " was change to Done");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "Success";
    }

//    public String userChecking(Long taskId, boolean checkResult, Principal principal) {
    public String userChecking(TaskForm taskForm, boolean checkResult, Principal principal) {
        try {
            UserDetails loginedUser = (UserDetails) ((Authentication) principal).getPrincipal();

            if(loginedUser.getAuthorities().isEmpty()) {
                return "Please login first";
            }

            TaskJob taskJob = taskJobRepository.findByTaskId(taskForm.getTaskId());
            if(taskJob != null) {
                if ((!loginedUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                        || !loginedUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MANAGER")))
                        && !userRepository.findAppUserByUserName(principal.getName()).getUserId()
                        .equals(taskJob.getTaskChecker())) {
                    return "You are not authorized to do this";
                }
            } else {
                return "Task not found";
            }

            if(checkResult) {
                taskJob.setTaskStatus("Checked");
            } else {
                taskJob.setTaskStatus("Remanded");
            }
            taskJobRepository.save(taskJob);
            LOGGER.info("TaskName " + taskJob.getTaskName() + " in jobId: " + taskJob.getJobTask().getJobId()
                    + " was checked by " + WebUtils.toString(loginedUser));

            JobLogic jobLogic = taskJob.getJobTask();
            if(taskJobRepository.findByTaskStatusAndJobTask("Pending", jobLogic).isEmpty()
                    && taskJobRepository.findByTaskStatusAndJobTask("Remanded", jobLogic).isEmpty() ) {
                jobLogic.setJobStatus("Done");
                jobLogicRepository.save(jobLogic);
                LOGGER.info("Status of JobName " + jobLogic.getJobName() + " was change to Done");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "Success";
    }
}
