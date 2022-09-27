package com.example.secure.appuser;

import com.example.secure.registration.email.EmailSender;
import com.example.secure.registration.email.EmailService;
import com.example.secure.registration.token.ConfirmationToken;
import com.example.secure.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {

    private final static String USER_NOT_FOUND_MSG = "user with email %s not found";
    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSender emailSender;
    private final EmailService emailService;



    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return appUserRepository.findByEmail(email)
                .orElseThrow(() ->
                    new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));
    }

    public String signUpUser(AppUser appUser){
        boolean userExist = appUserRepository.findByEmail(appUser.getEmail()).isPresent();
        if (userExist) throw new IllegalStateException("email already exist");

        String encodedPassword = bCryptPasswordEncoder.encode(appUser.getPassword());
        appUser.setPassword(encodedPassword);

        appUserRepository.save(appUser);

        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setToken(token);
        confirmationToken.setCreatedAt(LocalDateTime.now());
        confirmationToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        confirmationToken.setAppUser(appUser);

        confirmationTokenService.saveConfirmationToken(confirmationToken);



        String link = "http://localhost:8080/api/v1/registration/confirm?token="+token;
        emailSender.send(appUser.getEmail(),
                emailService.emailTemplate(
                        link,
                        appUser.getFirstName()+" "+appUser.getLastName()));

        return token;
    }

    public void saveEnabledAppUser(AppUser appUser){
        appUser.setEnabled(true);
        appUserRepository.save(appUser);
    }

}
