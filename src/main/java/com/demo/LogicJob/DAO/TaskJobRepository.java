package com.demo.LogicJob.DAO;

import com.demo.LogicJob.Entity.JobLogic;
import com.demo.LogicJob.Entity.TaskJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskJobRepository extends JpaRepository<TaskJob, Long> {
    List<TaskJob> findAllByJobTask (JobLogic jobLogic);

    TaskJob findByTaskIdAndJobTask(Long taskId, JobLogic jobTask);

    TaskJob findByTaskName (String taskName);

    TaskJob findByTaskNameAndJobTask(String taskName, JobLogic jobTask);

    List<TaskJob> findAllByJobTaskAndTaskWorker (JobLogic jobLogic, Long taskWorker);

    List<TaskJob> findAllByTaskWorkerOrderByJobTask (Long taskWorker);

    List<TaskJob> findAllByTaskCheckerOrderByJobTask (Long taskWorker);
}