package com.demo.LogicJob.Entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Job_Logic", //
        uniqueConstraints = { //
                @UniqueConstraint(name = "JOB_LOGIC_UK", columnNames = "Job_Name") })
public class JobLogic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Job_Id", nullable = false)
    private Long jobId;

    @Column(name = "Job_Name", length = 128, nullable = false)
    private String jobName;

    @Column(name = "job_Value", length = 128, nullable = false)
    private int jobValue;

    @Column(name = "job_Worker", length = 36, nullable = true)
    private Long jobWorker;

    @Column(name = "job_Checker", length = 36, nullable = true)
    private Long jobChecker;

    @Column(name = "job_Flow", length = 1, nullable = false)
    private boolean jobFlow;

    @Column(name = "job_Status", length = 36, nullable = false)
    private String jobStatus;

    @OneToMany(mappedBy="jobTask", cascade = CascadeType.ALL)
    private List<TaskJob> listTask;

    @ManyToOne
    @JoinColumn(name = "Worker_Id")
    private AppUser workerJob;

    @ManyToOne
    @JoinColumn(name = "Checker_Id")
    private AppUser checkerJob;

//    @ManyToOne(fetch=FetchType.EAGER)
//    @JoinTable(name = "Worker_Job",
//            joinColumns = { @JoinColumn(name = "Worker_Id") },
//            inverseJoinColumns = { @JoinColumn(name = "Job_Id") })
//    private AppUser workerJob;

//    @ManyToOne(fetch=FetchType.EAGER)
//    @JoinTable(name = "Checker_Job",
//            joinColumns = { @JoinColumn(name = "Checker_Id") },
//            inverseJoinColumns = { @JoinColumn(name = "Job_Id") })
//    private AppUser checkerJob;
}
