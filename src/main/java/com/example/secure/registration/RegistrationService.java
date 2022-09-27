package com.example.secure.registration;

import com.example.secure.appuser.AppUser;
import com.example.secure.appuser.AppUserRole;
import com.example.secure.appuser.AppUserService;
import com.example.secure.registration.token.ConfirmationToken;
import com.example.secure.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.example.secure.appuser.AppUserRole.USER;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final AppUserService appUserService;
    private final EmailValidator  emailValidator;
    private final ConfirmationTokenService confirmationTokenService;
    public String register(RegistrationRequest request) {
        boolean isValidEmail = emailValidator.test(request.getEmail());

        if (!isValidEmail) throw new IllegalStateException("Email is not valid");

        AppUser user = new AppUser();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setAppUserRole(USER);

        return appUserService.signUpUser(user);
    }

    @Transactional
    public String confirmToken(String token){
        //get the token
        ConfirmationToken confirmationToken = confirmationTokenService
                .getConfirmationToken(token)
                .orElseThrow(() -> new IllegalStateException("Token do not existe"));
        //check if is confirmed
        if (confirmationToken.getConfirmedAt() != null) throw new IllegalStateException("Token already confirmed");
        //check if is expired
        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) throw new IllegalStateException("Token expired");
        //save/update confirmed token
        confirmationTokenService.saveConfirmedTokenAt(confirmationToken);
        //save/update enabled user
        appUserService.saveEnabledAppUser(confirmationToken.getAppUser());

        return "Confirmed";
    }
}
