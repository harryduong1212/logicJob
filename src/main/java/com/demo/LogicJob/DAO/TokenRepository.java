package com.demo.LogicJob.DAO;

import com.demo.LogicJob.Entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findVerificationTokenByToken(String verificationToken);

}