package com.demo.LogicJob.Controller;

import com.demo.LogicJob.DAO.JobLogicRepository;
import com.demo.LogicJob.Entity.JobLogic;
import com.demo.LogicJob.Entity.TaskJob;
import com.demo.LogicJob.FormDTO.JobForm;
import com.demo.LogicJob.FormDTO.TaskForm;
import com.demo.LogicJob.Service.JobLogicService;
import com.demo.LogicJob.Service.UserMapperImpl;
import com.demo.LogicJob.Utils.WebUtils;
import com.demo.LogicJob.Validator.JobFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class JobController {

    @Autowired
    private JobLogicRepository jobLogicRepository;

    @Autowired
    private JobLogicService jobLogicService;

    @Autowired
    private JobFormValidator jobFormValidator;

    @InitBinder
    protected void initBinder(WebDataBinder dataBinder) {

        // Form target
        Object target = dataBinder.getTarget();
        if (target == null) {
            return;
        }
        System.out.println("Target=" + target);

        if (target.getClass() == JobForm.class) {
            dataBinder.setValidator(jobFormValidator);
        }
        // ...
    }


    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String adminPage(Model model, Principal principal) {

        // After user login successfully.
        String userName = principal.getName();
        System.out.println("User name: " + userName);
        UserDetails loginedUser = (UserDetails) ((Authentication) principal).getPrincipal();
        String userInfo = WebUtils.toString(loginedUser);
        model.addAttribute("userInfo", userInfo);
        model.addAttribute("newjob", new JobForm());
        List<JobLogic> jobList = jobLogicRepository.findAllByOrderByJobIdAsc();
        try {
            model.addAttribute("message", "Job List");
            model.addAttribute("joblist", jobList);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "adminPage";
    }

    @RequestMapping(value = "/admin", method = RequestMethod.POST)
    public String assignJob(Model model,
                            @ModelAttribute("newjob") @Validated JobForm jobForm,
                            BindingResult result,
                            Principal principal) {
        try {
            // Validation error.
            if (result.hasErrors()) {
                return "adminPage";
            }
            if(jobForm.getJobName() != null && !jobForm.getJobName().equals("")) {
//                String str = jobLogicService.createNewJob(jobForm.getJobName(), jobForm.isJobFlow(),
//                        jobForm.getJobWorker(), jobForm.getJobChecker(), principal);
                String str = jobLogicService.createNewJob(jobForm, principal);
                model.addAttribute("Message", str);
                model.addAttribute("ShowAllJob", jobLogicRepository.findAllByOrderByJobIdAsc());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            model.addAttribute("errorMessage", "Error " + ex.getMessage());
            return "adminPage";
        }
        jobForm.setJobName("");
        return "adminPage";
    }


}
