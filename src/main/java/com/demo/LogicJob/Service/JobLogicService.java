package com.demo.LogicJob.Service;

import com.demo.LogicJob.DAO.JobLogicRepository;
import com.demo.LogicJob.DAO.TaskJobRepository;
import com.demo.LogicJob.DAO.UserRepository;
import com.demo.LogicJob.Entity.AppUser;
import com.demo.LogicJob.Entity.JobLogic;
import com.demo.LogicJob.Entity.TaskJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JobLogicService {

    @Autowired
    private JobLogicRepository jobLogicRepository;

    @Autowired
    private TaskJobRepository taskJobRepository;

    @Autowired
    private UserRepository userRepository;

    public void createNewJob(String jobName, boolean jobFlow, String jobWorker, String jobChecker) {
        try {
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
    }

    public void createNewTask(Long jobId, String taskName) {
        try {
            JobLogic jobLogic = jobLogicRepository.findJobLogicByJobId(jobId);
            TaskJob taskJob = new TaskJob();
            taskJob.setJobTask(jobLogic);
            taskJob.setTaskName(taskName);
            taskJob.setTaskChecker(jobLogic.getJobChecker());
            taskJob.setTaskWorker(jobLogic.getJobWorker());
            taskJob.setTaskStatus("Pending");
            taskJobRepository.save(taskJob);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void userWorking(Long jobId, Long taskId, int taskValue) {
        try {
            JobLogic jobLogic = jobLogicRepository.findJobLogicByJobId(jobId);
            TaskJob taskJob = taskJobRepository.findByTaskIdAndJobTask(taskId, jobLogic);
            taskJob.setTaskValue(taskValue);
            taskJob.setTaskStatus("Done");
            taskJobRepository.save(taskJob);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
