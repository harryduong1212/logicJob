package com.demo.LogicJob.DAO;

import com.demo.LogicJob.Entity.JobLogic;
import com.demo.LogicJob.Entity.TaskJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TaskJobRepository extends JpaRepository<TaskJob, Long>, JpaSpecificationExecutor<TaskJob> {
    List<TaskJob> findAllByJobTask (JobLogic jobLogic);

    List<TaskJob> findAllOrderByJobTask(JobLogic jobTask);

    TaskJob findByTaskId(Long taskId);

    TaskJob findByTaskIdAndJobTask(Long taskId, JobLogic jobTask);

    TaskJob findByTaskName (String taskName);

    TaskJob findByTaskNameAndJobTask(String taskName, JobLogic jobTask);

    List<TaskJob> findAllByJobTaskAndTaskWorker (JobLogic jobLogic, Long taskWorker);

    List<TaskJob> findAllByTaskWorkerOrderByJobTask (Long taskWorker);

    List<TaskJob> findAllByTaskCheckerOrderByJobTask (Long taskWorker);

    @Transactional(readOnly = true)
    @Query("select tj " +
            "from TaskJob tj " +
            "inner join JobLogic jl " +
            "on tj.jobTask = jl.jobId " +
            "where tj.taskWorker = ?1 " +
            "and tj.taskStatus = ?2 " +
            "order by tj.jobTask asc")
    List<TaskJob> findAllByTaskWorkerAndStatusOrderByJobTask (Long taskWorker, String status);

    @Transactional(readOnly = true)
    @Query("select tj " +
            "from TaskJob tj " +
            "inner join JobLogic jl " +
            "on tj.jobTask = jl.jobId " +
            "where tj.taskChecker = ?1 " +
            "and tj.taskStatus = ?2 " +
            "order by tj.jobTask asc")
    List<TaskJob> findAllByTaskCheckerAndStatusOrderByJobTask (Long taskChecker, String status);

    List<TaskJob> findByTaskStatusAndJobTask(String taskStatus, JobLogic jobTask);

    List<TaskJob> findAllByJobTaskLike(JobLogic jobTask);

    @Transactional(readOnly = true)
    @Query("select tj " +
            "from TaskJob tj " +
            "inner join JobLogic jl " +
            "on tj.jobTask = jl.jobId " +
            "where jl.jobStatus = ?1 " +
            "or tj.taskStatus = ?1 ")
    List<TaskJob> findByTaskStatusOrJobStatus(String taskStatus);
}