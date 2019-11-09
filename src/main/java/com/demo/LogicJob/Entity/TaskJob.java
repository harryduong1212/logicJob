package com.demo.LogicJob.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Task_Job", //
        uniqueConstraints = { //
                @UniqueConstraint(name = "TASK_JOB_UK", columnNames = "Task_Name") })
public class TaskJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Task_Id", nullable = false)
    private Long taskId;

    @Column(name = "Task_Name", length = 128, nullable = false)
    private String taskName;

    @Column(name = "task_Value", length = 128, nullable = true)
    private int taskValue;

    @Column(name = "task_Worker", length = 36, nullable = true)
    private Long taskWorker;

    @Column(name = "task_Checker", length = 36, nullable = true)
    private Long taskChecker;

    @Column(name = "task_Status", length = 36, nullable = false)
    private String taskStatus;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "Task")
    private JobLogic jobTask;

}

