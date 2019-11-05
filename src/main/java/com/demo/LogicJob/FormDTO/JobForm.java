package com.demo.LogicJob.FormDTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobForm {
    private Long jobId;
    private Long jobChecker;
    private Long jobWorker;
    private String jobName;
    private String jobStatus;
    private int jobValue;

}
