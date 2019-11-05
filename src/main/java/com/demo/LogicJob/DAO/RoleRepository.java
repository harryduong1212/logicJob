package com.demo.LogicJob.DAO;

import com.demo.LogicJob.Entity.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<AppRole, Long> {
    AppRole findAppRoleByRoleName(String roleName);

}
