package com.demo.LogicJob.DAO;

import com.demo.LogicJob.Entity.AppUser;
import com.demo.LogicJob.Entity.JobLogic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface UserRepository extends JpaRepository<AppUser, Long>, JpaSpecificationExecutor<AppUser> {
    AppUser findAppUserByUserName(String userName);

    AppUser findAppUserByUserId(Long userId);

    AppUser findAppUserByEmail(String email);

    AppUser findRoleByUserId(Long userId);
}
