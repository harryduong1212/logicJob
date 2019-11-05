package com.demo.LogicJob.Service;

import com.demo.LogicJob.DAO.JobLogicRepository;
import com.demo.LogicJob.Entity.JobLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JobLogicService {

    @Autowired
    private JobLogicRepository jobLogicRepository;

    public void createNewJob(String jobName) {
        JobLogic jobLogic = new JobLogic();
        jobLogic.setJobName(jobName);
        jobLogic.setJobStatus("New");
        jobLogic.setJobValue(0);
        jobLogicRepository.save(jobLogic);
    }
}
