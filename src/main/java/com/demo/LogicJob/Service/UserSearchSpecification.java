package com.demo.LogicJob.Service;

import com.demo.LogicJob.Entity.AppUser;
import com.demo.LogicJob.Entity.JobLogic;
import com.demo.LogicJob.Entity.TaskJob;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.*;

public class UserSearchSpecification {

    public static Specification<AppUser> getUsersByJobId(String userName, Long jobId) {
        return new Specification<AppUser>() {
            @Override
            public Predicate toPredicate(Root<AppUser> root,
                                         CriteriaQuery<?> query,
                                         CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get(userName), jobId);
            }
        };
    }

    public static Specification<TaskJob> findAllTaskLike(String column, String searchKey) {
        return (Specification<TaskJob>) (root, query, criteriaBuilder) -> {
            String workComplete = "WorkComplete";
            if(workComplete.contains(searchKey)) {
                return criteriaBuilder.like(root.get(column).as(String.class), "%" + "Confirmed" + "%");
            }
            return criteriaBuilder.like(root.get(column).as(String.class),"%" + searchKey + "%");
        };
    }

    public static Specification<TaskJob> findAllJobLike(String column, String searchKey) {
        return (Specification<TaskJob>) (root, query, criteriaBuilder) -> {
            Join<JobLogic, TaskJob> taskJobJoin = root.join("jobTask");
            String waiting = "Waiting";
            String confirmingWorking = "ConfirmingWorking";
            if(waiting.contains(searchKey)) {
                return criteriaBuilder.like(taskJobJoin.get(column).as(String.class), "%" + "New" + "%");
            }
            if(confirmingWorking.contains(searchKey)) {
                return criteriaBuilder.like(taskJobJoin.get(column).as(String.class), "%" + "Inprogress" + "%");
            }
            return criteriaBuilder.like(taskJobJoin.get(column).as(String.class),"%" + searchKey + "%");
        };
    }

}
