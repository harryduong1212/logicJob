package com.demo.LogicJob.DAO;

import com.demo.LogicJob.FormDTO.AppUserForm;
import com.demo.LogicJob.Entity.AppUser;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper //Creates a Spring Bean automatically
public interface UserMapper {

    AppUserForm toUserForm(AppUser appUser);
    AppUser toAppUser(AppUserForm appUserForm);
    List<AppUserForm> toAppUserForm(List<AppUser> appUser);
}
