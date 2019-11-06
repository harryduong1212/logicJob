package com.demo.LogicJob.DAO;

import com.demo.LogicJob.Entity.AppUser;
import com.demo.LogicJob.Entity.JobLogic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobLogicRepository extends JpaRepository<JobLogic, Long> {
    JobLogic findJobLogicByJobId(Long jobId);

    List<JobLogic> findAllByOrderByJobIdAsc();

}
