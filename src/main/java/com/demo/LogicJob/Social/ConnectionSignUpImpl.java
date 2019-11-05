package com.demo.LogicJob.Social;

import com.demo.LogicJob.DAO.AppUserDAO;
import com.demo.LogicJob.DAO.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;

public class ConnectionSignUpImpl implements ConnectionSignUp {

    @Autowired
    private UserRepository userRepository;

    public ConnectionSignUpImpl(UserRepository userRepository) {
    }

    // After logging in social networking.
    // This method will be called to create a corresponding App_User record
    // if it does not already exist.
    @Override
    public String execute(Connection<?> connection) {
//        AppUser account = userDetailsServiceImpl.createAppUser(connection);
//        return account.getUserName();
        return "nothing";
    }
}
