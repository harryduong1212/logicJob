package com.demo.LogicJob.Service;

import com.demo.LogicJob.DAO.JobLogicRepository;
import com.demo.LogicJob.DAO.UserRepository;
import com.demo.LogicJob.Entity.AppUser;
import com.demo.LogicJob.Entity.JobLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JobLogicService {

    @Autowired
    private JobLogicRepository jobLogicRepository;

    @Autowired
    private UserRepository userRepository;

    public void createNewJob(String jobName) {
        JobLogic jobLogic = new JobLogic();
        jobLogic.setJobName(jobName);
        jobLogic.setJobStatus("New");
        jobLogic.setJobValue(0);
        jobLogicRepository.save(jobLogic);
    }

    public String assignUserJob(String jobWorker, String jobChecker, Long jobId) {
        try {
            JobLogic jobLogic = jobLogicRepository.findJobLogicByJobId(jobId);
            AppUser userWorker = new AppUser();
            AppUser userChecker = new AppUser();
            userChecker = userRepository.findAppUserByUserName(jobChecker);
            userWorker = userRepository.findAppUserByUserName(jobWorker);

            if(jobLogic != null) {
                jobLogic.setJobWorker(userWorker.getUserId());
                jobLogic.setJobChecker(userChecker.getUserId());
                jobLogic.setJobStatus("Inprogress");
                jobLogicRepository.save(jobLogic);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "Success";
    }
}
