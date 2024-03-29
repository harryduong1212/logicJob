package com.demo.LogicJob.Controller;

import com.demo.LogicJob.DAO.JobLogicRepository;
import com.demo.LogicJob.DAO.TaskJobRepository;
import com.demo.LogicJob.DAO.UserRepository;
import com.demo.LogicJob.Entity.JobLogic;
import com.demo.LogicJob.Entity.TaskJob;
import com.demo.LogicJob.FormDTO.SearchForm;
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
        String userRole = WebUtils.getRolsFormPrincipal(loginedUser);
        model.addAttribute("userrole", userRole);
        model.addAttribute("newtask", new TaskForm());
        model.addAttribute("ShowAllJob", jobLogicRepository.findAllByOrderByJobIdAsc());

        return "createTaskPage";
    }

    @RequestMapping(value = "/createtask", method = RequestMethod.POST)
    public String createTask(Model model,
                            @ModelAttribute("newtask") @Validated TaskForm taskForm,
                             BindingResult result,
                             Principal principal) {

        try {
            UserDetails loginedUser = (UserDetails) ((Authentication) principal).getPrincipal();
            String userRole = WebUtils.getRolsFormPrincipal(loginedUser);
            model.addAttribute("userrole", userRole);
            // Validation error.
            if (result.hasErrors()) {
                return "createTaskPage";
            }

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
        String userRole = WebUtils.getRolsFormPrincipal(loginedUser);
        List<TaskJob> taskJobs = taskJobRepository.findAllByTaskWorkerAndStatusOrderByJobTask(
                userRepository.findAppUserByUserName(userName).getUserId(), "Pending");
        taskJobs.addAll(taskJobRepository.findAllByTaskWorkerAndStatusOrderByJobTask(
                userRepository.findAppUserByUserName(userName).getUserId(), "Remanded"));
        model.addAttribute("userrole", userRole);
        model.addAttribute("taskform", new TaskForm(0L));
        model.addAttribute("tasklist", taskJobs);
        return "workingPage";
    }

    @RequestMapping(value = "/working", method = RequestMethod.POST)
    public String workingDone(Model model,
                             @ModelAttribute("taskform") @Validated TaskForm taskForm,
                              BindingResult result,
                              Principal principal) {
        try {
            UserDetails loginedUser = (UserDetails) ((Authentication) principal).getPrincipal();
            String userRole = WebUtils.getRolsFormPrincipal(loginedUser);
            model.addAttribute("userrole", userRole);
            // Validation error.
            if (result.hasErrors()) {
                return "workingPage";
            }

            String str = jobLogicService.userWorking(taskForm, principal);
            model.addAttribute("Message", str);
            String userName = principal.getName();
            List<TaskJob> taskJobs = taskJobRepository.findAllByTaskWorkerOrderByJobTask(
                    userRepository.findAppUserByUserName(userName).getUserId());
            model.addAttribute("tasklist", taskJobs);
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
        String userRole = WebUtils.getRolsFormPrincipal(loginedUser);
        List<TaskJob> taskJobs = taskJobRepository.findAllByTaskCheckerAndStatusOrderByJobTask(
                userRepository.findAppUserByUserName(userName).getUserId(), "Confirmed");

        model.addAttribute("userrole", userRole);
        model.addAttribute("taskform", new TaskForm(0L, 0L));
        model.addAttribute("tasklist", taskJobs);
        return "checkingPage";
    }

    @RequestMapping(value = "/checking", method = RequestMethod.POST)
    public String checkingDone(Model model,
                              @ModelAttribute("taskform") @Validated TaskForm taskForm,
                               @RequestParam(value="action", required=true) String action,
                               BindingResult result,
                               Principal principal) {

        try {
            UserDetails loginedUser = (UserDetails) ((Authentication) principal).getPrincipal();
            String userRole = WebUtils.getRolsFormPrincipal(loginedUser);
            model.addAttribute("userrole", userRole);
            // Validation error.
            if (result.hasErrors()) {
                return "checkingPage";
            }

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
            model.addAttribute("tasklist", taskJobs);
        } catch (Exception ex) {
            model.addAttribute("errorMessage", "Error " + ex.getMessage());
            ex.printStackTrace();
            return "checkingPage";
        }

        return "checkingPage";
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String searchPage(Model model, Principal principal) {

        //After user login successfully.
        String userName = principal.getName();
        System.out.println("User name: " + userName);
        UserDetails loginedUser = (UserDetails) ((Authentication) principal).getPrincipal();
        String userRole = WebUtils.getRolsFormPrincipal(loginedUser);
        model.addAttribute("userrole", userRole);
        model.addAttribute("search", new SearchForm());
        model.addAttribute("showAllTask", jobLogicService.getAllTask(null));

        return "searchPage";
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public String search(Model model,
                             @ModelAttribute("search") @Validated SearchForm searchForm,
                             BindingResult result,
                             Principal principal) {
        try {
            UserDetails loginedUser = (UserDetails) ((Authentication) principal).getPrincipal();
            String userRole = WebUtils.getRolsFormPrincipal(loginedUser);
            model.addAttribute("userrole", userRole);
            // Validation error.
            if (result.hasErrors()) {
                return "searchPage";
            }
            List<TaskForm> taskFormList = jobLogicService.searchByKey(searchForm);
            model.addAttribute("showAllTask", jobLogicService.getAllTask(taskFormList));
        } catch (Exception ex) {
            model.addAttribute("errorMessage", "Error " + ex.getMessage());
            ex.printStackTrace();
            return "searchPage";
        }

        return "searchPage";
    }
}
