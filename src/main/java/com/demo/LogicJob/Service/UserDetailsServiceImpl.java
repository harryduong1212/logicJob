package com.demo.LogicJob.Service;

import com.demo.LogicJob.DAO.TokenRepository;
import com.demo.LogicJob.DAO.UserRepository;
import com.demo.LogicJob.Entity.AppRole;
import com.demo.LogicJob.Entity.AppUser;
import com.demo.LogicJob.Entity.VerificationToken;
import com.demo.LogicJob.Event.OnRegistrationSuccessEvent;
import com.demo.LogicJob.FormDTO.AppUserForm;
import com.demo.LogicJob.Social.SocialUserDetailsImpl;
import com.demo.LogicJob.Utils.EncrytedPasswordUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.connect.*;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private ConnectionFactoryLocator connectionFactoryLocator;

    @Autowired
    private UsersConnectionRepository connectionRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private UserMapperImpl userMapperImpl;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsServiceImpl.class);


    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

        System.out.println("UserDetailsServiceImpl.loadUserByUsername=" + userName);

        AppUser appUser = userRepository.findAppUserByUserName(userName);
        LOGGER.info("User "+ userName + " has been found!");
        if (appUser == null) {
            System.out.println("User not found! " + userName);
            LOGGER.error("User "+ userName + " was not found in the database!");
            throw new UsernameNotFoundException("User " + userName + " was not found in the database");
        }

        System.out.println("Found User: " + appUser);

        // [ROLE_USER, ROLE_ADMIN,..]
        String roleNames = userRepository.findRoleByUserId(appUser.getUserId()).getRoles().getRoleName();

        List<GrantedAuthority> grantList = new ArrayList<GrantedAuthority>();
        GrantedAuthority authority = new SimpleGrantedAuthority(roleNames);
        grantList.add(authority);

        //SocialUserDetailsImpl userDetails = new SocialUserDetailsImpl(appUser, roleNames);

        return new SocialUserDetailsImpl(appUser, roleNames);
    }

    public void createRoleFor(AppUser appUser, String roleNames) {
        //
        AppRole appRole = new AppRole(2L, "ROLE_ADMIN");
        appUser.setRoles(appRole);
        userRepository.save(appUser);
    }

    // Auto create App User Account.
    public AppUser createAppUser(Connection<?> connection, WebRequest request) {
        ProviderSignInUtils providerSignInUtils //
                = new ProviderSignInUtils(connectionFactoryLocator, connectionRepository);
        ConnectionKey key = connection.getKey();
        // (facebook,12345), (google,123) ...

        System.out.println("key= (" + key.getProviderId() + "," + key.getProviderUserId() + ")");

        UserProfile socialUserProfile = connection.fetchUserProfile();

        String email = socialUserProfile.getEmail();
        AppUser appUser = userRepository.findAppUserByEmail(email);
        if (appUser != null) {
            providerSignInUtils.doPostSignUp(appUser.getUserName(), request);
            LOGGER.info("User " + appUser.getUserName() + " has been found! Login through " + key.getProviderId());
            return appUser;
        }

        String randomPassword = UUID.randomUUID().toString().substring(0, 8);
        String encrytedPassword = EncrytedPasswordUtils.encrytePassword(randomPassword);

        appUser = new AppUser();
        appUser.setEnabled(true);
        appUser.setEncrytedPassword(encrytedPassword);
        appUser.setUserName(email);
        appUser.setEmail(email);
        appUser.setFirstName(socialUserProfile.getFirstName());
        appUser.setLastName(socialUserProfile.getLastName());

        this.entityManager.persist(appUser);

        // Create default Role
        this.createRoleFor(appUser, "");

        Locale locale = Locale.ENGLISH;
        eventPublisher.publishEvent(new OnRegistrationSuccessEvent(appUser, locale, "", randomPassword));
        providerSignInUtils.doPostSignUp(appUser.getUserName(), request);

        LOGGER.info("User " + appUser.getUserName() + " has been created through social!");

        return appUser;
    }

    public AppUser registerNewUserAccount(AppUserForm appUserForm, String roleNames, WebRequest request) {
        AppUser appUser = new AppUser();
        appUser.setUserName(appUserForm.getUserName());
        appUser.setEmail(appUserForm.getEmail());
        appUser.setFirstName(appUserForm.getFirstName());
        appUser.setLastName(appUserForm.getLastName());
        appUser.setEnabled(false);
        String encrytedPassword = EncrytedPasswordUtils.encrytePassword(appUserForm.getPassword());
        appUser.setEncrytedPassword(encrytedPassword);
        this.entityManager.persist(appUser);
        this.entityManager.flush();
        this.createRoleFor(appUser, roleNames);
        LOGGER.info("User " + appUser.getUserName() + " has been created!");


        String appUrl = request.getContextPath();
        eventPublisher.publishEvent(new OnRegistrationSuccessEvent(appUser, request.getLocale(),appUrl, ""));

        return appUser;
    }

    public void createVerificationToken(AppUser appUser, String token) {
        VerificationToken newUserToken = new VerificationToken(token, appUser);
        LOGGER.info("Verification token of user " + appUser.getUserName() + " has been created!");
        tokenRepository.save(newUserToken);
    }

    public VerificationToken getVerificationToken(String verificationToken) {
        return tokenRepository.findVerificationTokenByToken(verificationToken);
    }

    public void enableRegisteredUser(AppUser appUser) {
        appUser.setEnabled(true);
        LOGGER.info("User " + appUser.getUserName() + " is actived!");
        userRepository.save(appUser);
    }

    //@Cacheable(value="serialize", keyGenerator="customKeyGenerator")
    public String testSerialize(AppUserForm appUserForm) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        //module.addSerializer(AppUserForm.class, new CustomUserSerialize());
        //mapper.registerModule(module);


        String serialized = mapper.writeValueAsString(appUserForm);
        //simulateSlowService();
        return serialized;
    }

    //@Cacheable(value="deserialize", keyGenerator="customKeyGenerator")
    public AppUserForm testDeserialize(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        //module.addDeserializer(AppUserForm.class, new CustomUserDeserializer());
        //mapper.registerModule(module);

        AppUserForm appUserForm = mapper.readValue(json, AppUserForm.class);
        //simulateSlowService();
        return appUserForm;
    }

    public List<AppUserForm> getAllUsers() {
        List<AppUser> appUserList = userRepository.findAll();
        return userMapperImpl.toAppUserForm(appUserList);
    }
}
