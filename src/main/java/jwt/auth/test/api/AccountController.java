package jwt.auth.test.api;

import jwt.auth.test.dto.UserDto;
import jwt.auth.test.entity.User;
import jwt.auth.test.entity.VerificationToken;
import jwt.auth.test.service.AccountService;
import jwt.auth.test.service.event.OnUserRegisterEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class AccountController {

    private final AccountService accountService;
    private final ApplicationEventPublisher eventPublisher;

    @GetMapping("/me")
    public ResponseEntity<UserDetails> getUserInfo(Authentication authentication) {
        if (authentication instanceof OAuth2Authentication) {
            return ResponseEntity.ok(accountService.loadUserByUsername(authentication.getPrincipal().toString()));
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping(path = "/confirm")
    public String confirmRegistration(@RequestParam("token") String token) {
        VerificationToken verificationToken = accountService.retrieveToken(token);
        if (verificationToken.isExpired()) {
            return "Token is expired";
        }
        accountService.activateAccount(verificationToken);

        return "Success";
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable("id") Long userId) {
        log.info("Deleting user ID [{}]", userId);
        accountService.deleteAccount(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public User createUser(@RequestBody UserDto userDto) {
        User user = accountService.save(userDto);
        OnUserRegisterEvent userRegisterEvent = new OnUserRegisterEvent(user);
        log.debug("publishing user register event: [{}]", userRegisterEvent);
        eventPublisher.publishEvent(userRegisterEvent);
        return user;
    }

}
