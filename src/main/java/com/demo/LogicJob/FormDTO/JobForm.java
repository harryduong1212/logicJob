package com.demo.LogicJob.FormDTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobForm {
    private Long jobId;
    private Long jobCheckerId;
    private Long jobWorkerId;
    private String jobChecker;
    private String jobWorker;
    private String jobName;
    private String jobStatus;
    private boolean jobFlow;
    private int jobValue;

}
