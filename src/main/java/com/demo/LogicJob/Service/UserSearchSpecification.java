package com.demo.LogicJob.Service;

import com.demo.LogicJob.Entity.AppUser;
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

    public static Specification<AppUser> getUsersByEmailSpec(String email, String searchKey) {
        return (Specification<AppUser>) (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get(email), searchKey);
        };
    }

    public static Specification<AppUser> getUsersByUserIdSpec(String userId, Long searchKey) {
        return (Specification<AppUser>) (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get(userId), searchKey);
        };
    }
}
