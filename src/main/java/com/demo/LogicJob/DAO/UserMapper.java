package com.demo.LogicJob.DAO;

import com.demo.LogicJob.Entity.JobLogic;
import com.demo.LogicJob.Entity.TaskJob;
import com.demo.LogicJob.FormDTO.AppUserForm;
import com.demo.LogicJob.Entity.AppUser;
import com.demo.LogicJob.FormDTO.JobForm;
import com.demo.LogicJob.FormDTO.TaskForm;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper //Creates a Spring Bean automatically
public interface UserMapper {

    JobLogic toJobLogic(JobForm jobForm);
    JobForm toJobForm(JobLogic jobLogic);

    TaskJob toTaskJob(TaskForm taskForm);
    TaskForm toTaskForm(TaskJob taskJob);

    AppUserForm toUserForm(AppUser appUser);
    AppUser toAppUser(AppUserForm appUserForm);
    List<AppUserForm> toAppUserForm(List<AppUser> appUser);
    List<TaskForm> toTaskFormList(List<TaskJob> taskJobsList);
}
