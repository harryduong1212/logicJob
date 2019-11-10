package com.demo.LogicJob.Controller;

import com.demo.LogicJob.DAO.JobLogicRepository;
import com.demo.LogicJob.DAO.TaskJobRepository;
import com.demo.LogicJob.DAO.UserRepository;
import com.demo.LogicJob.Entity.JobLogic;
import com.demo.LogicJob.Entity.TaskJob;
import com.demo.LogicJob.FormDTO.TaskForm;
import com.demo.LogicJob.Service.JobLogicService;
import com.demo.LogicJob.Service.UserMapperImpl;
import com.demo.LogicJob.Service.UserSearchSpecification;
import com.demo.LogicJob.Utils.WebUtils;
import com.demo.LogicJob.Validator.TaskFormValidation;
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
import java.util.List;

@Controller
public class TaskController {
    @Autowired
    private TaskJobRepository taskJobRepository;

    @Autowired
    private JobLogicRepository jobLogicRepository;

    @Autowired
    private JobLogicService jobLogicService;

    @Autowired
    private TaskFormValidation taskFormValidation;

    @Autowired
    private UserRepository userRepository;

    @InitBinder
    protected void initBinder(WebDataBinder dataBinder) {

        // Form target
        Object target = dataBinder.getTarget();
        if (target == null) {
            return;
        }
        System.out.println("Target=" + target);

        if (target.getClass() == TaskForm.class) {
            dataBinder.setValidator(taskFormValidation);
        }


        // ...
    }

    @RequestMapping(value = "/createtask", method = RequestMethod.GET)
    public String createTaskPage(Model model, Principal principal) {

        //After user login successfully.
        String userName = principal.getName();
        System.out.println("User name: " + userName);
        UserDetails loginedUser = (UserDetails) ((Authentication) principal).getPrincipal();
        String userInfo = WebUtils.toString(loginedUser);
        model.addAttribute("userInfo", userInfo);
        model.addAttribute("newtask", new TaskForm());

        return "createTaskPage";
    }

    @RequestMapping(value = "/createtask", method = RequestMethod.POST)
    public String createTask(Model model,
                            @ModelAttribute("newtask") @Validated TaskForm taskForm,
                             BindingResult result,
                             Principal principal) {
        // Validation error.
        if (result.hasErrors()) {
            return "createTaskPage";
        }

        try {
            String str = jobLogicService.createNewTask(taskForm, principal);
            model.addAttribute("showAllTask", taskJobRepository.findAllByJobTask(
                    jobLogicRepository.findJobLogicByJobId( taskForm.getJobId() )));
            model.addAttribute("Message", str);
        } catch (Exception ex) {
            model.addAttribute("errorMessage", "Error " + ex.getMessage());
            ex.printStackTrace();
            return "createTaskPage";
        }

        return "createTaskPage";
    }

    @RequestMapping(value = "/working", method = RequestMethod.GET)
    public String workingPage(Model model, Principal principal) {

        //After user login successfully.
        String userName = principal.getName();
        System.out.println("User name: " + userName);
        UserDetails loginedUser = (UserDetails) ((Authentication) principal).getPrincipal();
        String userInfo = WebUtils.toString(loginedUser);
        List<TaskJob> taskJobs = taskJobRepository.findAllByTaskWorkerAndStatusOrderByJobTask(
                userRepository.findAppUserByUserName(userName).getUserId(), "Pending");
        taskJobs.addAll(taskJobRepository.findAllByTaskWorkerAndStatusOrderByJobTask(
                userRepository.findAppUserByUserName(userName).getUserId(), "Remanded"));
        model.addAttribute("userInfo", userInfo);
        model.addAttribute("taskform", new TaskForm(1L));
        model.addAttribute("tasklist", taskJobs);
        return "workingPage";
    }

    @RequestMapping(value = "/working", method = RequestMethod.POST)
    public String workingDone(Model model,
                             @ModelAttribute("taskform") @Validated TaskForm taskForm,
                              BindingResult result,
                              Principal principal) {
        // Validation error.
        if (result.hasErrors()) {
            return "workingPage";
        }

        try {
            String str = jobLogicService.userWorking(taskForm, principal);
            model.addAttribute("Message", str);
            String userName = principal.getName();
            List<TaskJob> taskJobs = taskJobRepository.findAllByTaskWorkerAndStatusOrderByJobTask(
                    userRepository.findAppUserByUserName(userName).getUserId(), "Confirmed");
            taskJobs.addAll(taskJobRepository.findAllByTaskWorkerAndStatusOrderByJobTask(
                    userRepository.findAppUserByUserName(userName).getUserId(), "Checked"));
            model.addAttribute("tasklist2", taskJobs);
        } catch (Exception ex) {
            model.addAttribute("errorMessage", "Error " + ex.getMessage());
            ex.printStackTrace();
            return "workingPage";
        }

        return "workingPage";
    }

    @RequestMapping(value = "/checking", method = RequestMethod.GET)
    public String checkingPage(Model model, Principal principal) {

        //After user login successfully.
        String userName = principal.getName();
        System.out.println("User name: " + userName);
        UserDetails loginedUser = (UserDetails) ((Authentication) principal).getPrincipal();
        String userInfo = WebUtils.toString(loginedUser);
        List<TaskJob> taskJobs = taskJobRepository.findAllByTaskCheckerAndStatusOrderByJobTask(
                userRepository.findAppUserByUserName(userName).getUserId(), "Confirmed");

        model.addAttribute("userInfo", userInfo);
        model.addAttribute("taskform", new TaskForm(1L));
        model.addAttribute("tasklist", taskJobs);
        return "checkingPage";
    }

    @RequestMapping(value = "/checking", method = RequestMethod.POST)
    public String checkingDone(Model model,
                              @ModelAttribute("taskform") @Validated TaskForm taskForm,
                               @RequestParam(value="action", required=true) String action,
                               BindingResult result,
                               Principal principal) {
        // Validation error.
        if (result.hasErrors()) {
            return "checkingPage";
        }

        try {
            String str;
            if (action.equals("Accept")) {
                str = jobLogicService.userChecking(taskForm, true, principal);
            } else {
                str = jobLogicService.userChecking(taskForm, false, principal);
            }
            model.addAttribute("Message", str);
            String userName = principal.getName();
            List<TaskJob> taskJobs = taskJobRepository.findAllByTaskCheckerAndStatusOrderByJobTask(
                    userRepository.findAppUserByUserName(userName).getUserId(), "Checked");
            taskJobs.addAll(taskJobRepository.findAllByTaskCheckerAndStatusOrderByJobTask(
                    userRepository.findAppUserByUserName(userName).getUserId(), "Remanded"));
            model.addAttribute("tasklist2", taskJobs);
        } catch (Exception ex) {
            model.addAttribute("errorMessage", "Error " + ex.getMessage());
            ex.printStackTrace();
            return "checkingPage";
        }

        return "checkingPage";
    }

}
