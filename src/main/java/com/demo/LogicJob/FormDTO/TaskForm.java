package com.demo.LogicJob.FormDTO;

import com.demo.LogicJob.Entity.JobLogic;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskForm {
    private Long taskId;
    private Long JobId;
    private String taskName;
    private int taskValue;
    private Long taskWorker;
    private Long taskChecker;
    private String taskStatus;
    private JobForm jobForm;

    public TaskForm(Long jobId) {
        this.JobId = jobId;
    }

    public TaskForm(Long jobId, Long taskId) {
        this.JobId = jobId;
        this.taskId = taskId;
    }
}
