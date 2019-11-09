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

    public static Specification<JobLogic> getJobLogicByUserName(String userType, String userName) {
        return (Specification<JobLogic>) (root, query, criteriaBuilder)
                -> criteriaBuilder.equal(root.get(userType), userName);
    }

    public static Specification<TaskJob> getTaskListByJobLogic(String jobTask, Long jobId) {
        return new Specification<TaskJob>() {
            @Override
            public Predicate toPredicate(Root<TaskJob> root,
                                         CriteriaQuery<?> query,
                                         CriteriaBuilder criteriaBuilder) {

                Predicate predicate = criteriaBuilder.equal(root.get(jobTask), jobId);
                Predicate predicate1 = criteriaBuilder.equal(root.get(jobTask), jobId);
                criteriaBuilder.and(predicate, predicate1);
                return  criteriaBuilder.and(predicate, predicate1);
            }
        };
    }
}
