package com.demo.LogicJob.Controller;

import com.demo.LogicJob.DAO.JobLogicRepository;
import com.demo.LogicJob.Entity.AppUser;
import com.demo.LogicJob.Entity.JobLogic;
import com.demo.LogicJob.FormDTO.AppUserForm;
import com.demo.LogicJob.FormDTO.JobForm;
import com.demo.LogicJob.Service.JobLogicService;
import com.demo.LogicJob.Utils.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
public class JobController {

    @Autowired
    private JobLogicRepository jobLogicRepository;

    @Autowired
    private JobLogicService jobLogicService;


    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String adminPage(@RequestParam(value = "search", required = false) String searchU,
                            Model model, Principal principal) {

        // After user login successfully.
        String userName = principal.getName();
        System.out.println("User name: " + userName);
        UserDetails loginedUser = (UserDetails) ((Authentication) principal).getPrincipal();
        String userInfo = WebUtils.toString(loginedUser);
        model.addAttribute("userInfo", userInfo);
        model.addAttribute("newjob", new JobForm());

        List<JobLogic> jobList = new ArrayList<>();
        List<JobLogic> searchResults = new ArrayList<>();
        jobList = jobLogicRepository.findAllByOrderByJobIdAsc();
        try {
            model.addAttribute("message", "Job List");
            model.addAttribute("joblist", jobList);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "adminPage";
    }

    @RequestMapping(value = "/admin", method = RequestMethod.POST)
    public String createJob(Model model,
                            @ModelAttribute("newjob") JobForm jobForm) {
        try {
            if(jobForm.getJobName() != null && jobForm.getJobName() != "") {
                jobLogicService.createNewJob(jobForm.getJobName());
                model.addAttribute("Message", "Create job successfully");
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
