package com.demo.LogicJob.FormDTO;

import lombok.*;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.UserProfile;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppUserForm {

    private Long userId;
    private String email;
    private String userName;
    private boolean enable;
    private String firstName;
    private String lastName;
    private String password;
    private String role;
    private String signInProvider;
    private String providerUserId;

    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z", timezone="GMT")
    private Date update;

    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z", timezone="GMT")
    private Date create;


    public AppUserForm(Connection<?> connection) {
        UserProfile socialUserProfile = connection.fetchUserProfile();
        this.userId = null;
        this.email = socialUserProfile.getEmail();
        this.userName = socialUserProfile.getUsername();
        this.firstName = socialUserProfile.getFirstName();
        this.lastName = socialUserProfile.getLastName();

        ConnectionKey key = connection.getKey();
        // google, facebook, twitter
        this.signInProvider = key.getProviderId();

        // ID of User on google, facebook, twitter.
        // ID của User trên google, facebook, twitter.
        this.providerUserId = key.getProviderUserId();
    }

    public String toFormString() {
        return "UserId: " + this.userId + " \n" +
                "Username: " + this.userName + " \n" +
                "Email: " + this.email + " \n" +
                "Enable: " + this.enable + " \n" +
                "Firstname: " + this.firstName + " \n" +
                "Lastname: " + this.lastName + " \n" +
                "Role: " + this.role + " \n" +
                "Update: " + this.update.toString() + " \n" +
                "Create: " + this.create.toString() + " \n";
    }
}
