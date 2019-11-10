package com.demo.LogicJob.FormDTO;

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

    public TaskForm(Long jobId) {
        this.JobId = jobId;
    }
}
