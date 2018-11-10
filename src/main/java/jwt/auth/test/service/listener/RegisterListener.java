package jwt.auth.test.service.listener;

import jwt.auth.test.api.AccountController;
import jwt.auth.test.entity.User;
import jwt.auth.test.entity.VerificationToken;
import jwt.auth.test.service.AccountService;
import jwt.auth.test.service.EmailService;
import jwt.auth.test.service.event.OnUserRegisterEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

@Slf4j
@Component
public class RegisterListener implements ApplicationListener<OnUserRegisterEvent> {

    private EmailService emailService;
    private AccountService accountService;

    RegisterListener(
            EmailService emailService,
            AccountService accountService
    ) {
        this.emailService = emailService;
        this.accountService = accountService;
    }

    @Override
    public void onApplicationEvent(OnUserRegisterEvent event) {
        log.info("fired user created event: [{}]", event);
        User user = event.getUser();
        VerificationToken verificationToken = accountService.createVerificationToken(user);
        UriComponents uriComponents = MvcUriComponentsBuilder
                .fromMethodName(AccountController.class, "confirmRegistration", verificationToken.getToken()).build();
        String uri = uriComponents.encode().toUriString();

        emailService.sendSimpleMessage(user.getEmail(), "Confirm Registration", "<a href=\"" + uri + "\">" + uri + "</a>");
    }
}
