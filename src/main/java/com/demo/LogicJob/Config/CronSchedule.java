package com.demo.LogicJob.Config;

import com.demo.LogicJob.DAO.UserRepository;
import com.demo.LogicJob.Entity.AppUser;
import com.demo.LogicJob.FormDTO.AppUserForm;
import com.demo.LogicJob.Service.UserDetailsServiceImpl;
import com.demo.LogicJob.Service.UserMapperImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CronSchedule {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    private UserMapperImpl userMapperImpl;

    private static final Logger LOGGER = LoggerFactory.getLogger(CronSchedule.class);

    @Async
    @Scheduled(cron = "0 30 * * * *", zone = "JST")
    public void collectAllUser() throws JsonProcessingException {
        System.out.println("********************************************");
        List<AppUser> appUserList = userRepository.findAll();
        for( AppUser x : appUserList) {
            AppUserForm temp;
            temp = userMapperImpl.toUserForm(x);
            String serialize = userDetailsServiceImpl.testSerialize(temp);
            System.out.println( serialize );
            AppUserForm appUserForm = userDetailsServiceImpl.testDeserialize(serialize);
            System.out.println( appUserForm.toFormString() );
            //LOGGER.info(x.getUserName());
        }
    }

}
