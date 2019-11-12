package com.demo.LogicJob.FormDTO;

import lombok.*;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class SearchForm {
    private String searchKey;
    private List<String> searchZone;

    public SearchForm(){
        searchZone = Arrays.asList("taskId", "taskName", "taskWorker",
                "taskChecker", "taskValue", "taskStatus", "jobId",
                "jobName", "jobStatus", "jobFlow");
    }
}
