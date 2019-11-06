package com.demo.LogicJob.Controller;

import com.demo.LogicJob.Config.CronSchedule;
import com.demo.LogicJob.Entity.AppRole;
import com.demo.LogicJob.Entity.AppUser;
import com.demo.LogicJob.Entity.VerificationToken;
import com.demo.LogicJob.Utils.*;
import com.demo.LogicJob.FormDTO.AppUserForm;
import com.demo.LogicJob.Service.UserDetailsServiceImpl;
import com.demo.LogicJob.Validator.FormValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.constraints.NotNull;
import java.security.Principal;
import java.util.Calendar;
import java.util.Locale;

@Controller
@EnableScheduling
public class SecurityController {
    private CronSchedule cronSchedule;

    @Autowired
    private ConnectionFactoryLocator connectionFactoryLocator;

    @Autowired
    private UsersConnectionRepository connectionRepository;

    @Autowired
    private FormValidator formValidator;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    private MessageSource messageSource;

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityController.class);

    @InitBinder
    protected void initBinder(WebDataBinder dataBinder) {

        // Form target
        Object target = dataBinder.getTarget();
        if (target == null) {
            return;
        }
        System.out.println("Target=" + target);

        if (target.getClass() == AppUserForm.class) {
            dataBinder.setValidator(formValidator);
        }
        // ...
    }

    @RequestMapping(value = { "/", "/welcome" }, method = RequestMethod.GET)
    public String welcomePage(Model model) {
        model.addAttribute("title", "Welcome");
        model.addAttribute("message", "This is welcome page!");
        return "welcomePage";
    }

    @RequestMapping(value = "/logoutSuccessful", method = RequestMethod.GET)
    public String logoutSuccessfulPage(Model model) {
        model.addAttribute("title", "Logout");
        return "logoutSuccessfulPage";
    }

    @RequestMapping(value = "/userInfo", method = RequestMethod.GET)
    public String userInfo(Model model, Principal principal) {

        // After user login successfully.
        String userName = principal.getName();

        System.out.println("User Name: " + userName);

        UserDetails loginedUser = (UserDetails) ((Authentication) principal).getPrincipal();

        String userInfo = WebUtils.toString(loginedUser);
        model.addAttribute("userInfo", userInfo);

        return "userInfoPage";
    }

    @RequestMapping(value = "/403", method = RequestMethod.GET)
    public String accessDenied(Model model, Principal principal) {

        if (principal != null) {
            UserDetails loginedUser = (UserDetails) ((Authentication) principal).getPrincipal();

            String userInfo = WebUtils.toString(loginedUser);

            model.addAttribute("userInfo", userInfo);

            String message = "Hi " + principal.getName() //
                    + "<br> You do not have permission to access this page!";
            model.addAttribute("message", message);

        }

        return "403Page";
    }

    @RequestMapping(value = { "/login" }, method = RequestMethod.GET)
    public String login(Model model) {
        return "loginPage";
    }

    // User login with social networking,
    // but does not allow the app to view basic information
    // application will redirect to page / signin.
    @RequestMapping(value = { "/signin" }, method = RequestMethod.GET)
    public String signInPage(Model model) {
        return "redirect:/login";
    }

    @RequestMapping(value = { "/signup" }, method = RequestMethod.GET)
    public String signupPage(WebRequest request, Model model) {

        ProviderSignInUtils providerSignInUtils //
                = new ProviderSignInUtils(connectionFactoryLocator, connectionRepository);

        // Retrieve social networking information.
        Connection<?> connection = providerSignInUtils.getConnectionFromSession(request);
        //
        AppUserForm myForm = null;
        //
        if (connection != null) {
            //myForm = new AppUserForm(connection);
            AppUser registered = userDetailsServiceImpl.createAppUser(connection, request);
            AppRole appRole = new AppRole(2L, "ROLE_ADMIN");
            SecurityUtil.logInUser(registered, appRole.getRoleName());
            return "redirect:/userInfo";
        } else {
            myForm = new AppUserForm();
        }
        model.addAttribute("myForm", myForm);
        return "signupPage";
    }

    @RequestMapping(value = { "/signup" }, method = RequestMethod.POST)
    public String signupSave(WebRequest request, //
                             Model model, //
                             @ModelAttribute("myForm") @Validated AppUserForm appUserForm, //
                             BindingResult result, //
                             final RedirectAttributes redirectAttributes) {

            // Validation error.
            if (result.hasErrors()) {
                return "signupPage";
            }

        AppRole appRole = new AppRole(1L, "ROLE_USER");

        AppUser registered = null;

        try {
            registered = userDetailsServiceImpl.registerNewUserAccount(appUserForm, appRole.getRoleName(), request);
            String str = messageSource.getMessage("message.registrationSuccess", null, Locale.forLanguageTag("en"));
            model.addAttribute("messages", str + " " + registered.getUserName());
        } catch (Exception ex) {
            ex.printStackTrace();
            model.addAttribute("errorMessage", "Error " + ex.getMessage());
            return "signupPage";
        }

        // After registration is complete, automatic login.
        SecurityUtil.logInUser(registered, appRole.getRoleName());

        return "redirect:/userInfo";
    }

    @RequestMapping(value = "/confirmRegistration", method = RequestMethod.GET)
    public String confirmRegistration(@NotNull WebRequest request, Model model, @RequestParam("token") String token) {
        Locale locale=request.getLocale();
        VerificationToken verificationToken = userDetailsServiceImpl.getVerificationToken(token);
        if(verificationToken == null) {
            String message = messageSource.getMessage("auth.message.invalidToken", null, locale);
            model.addAttribute("message", message);
            return "redirect:403";
        }
        AppUser appUser = verificationToken.getUser();
        Calendar calendar = Calendar.getInstance();
        if((verificationToken.getExpiryDate().getTime()-calendar.getTime().getTime())<=0) {
            String message = messageSource.getMessage("auth.message.expired", null, locale);
            model.addAttribute("message", message);
            return "redirect:403";
        }

        String message = messageSource.getMessage("message.registrationSuccessConfimationLink", null, locale);
        model.addAttribute("message", message);

        userDetailsServiceImpl.enableRegisteredUser(appUser);
        return "welcomePage";
    }

}
