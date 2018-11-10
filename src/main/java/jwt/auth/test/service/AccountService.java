package jwt.auth.test.service;

import jwt.auth.test.dto.UserDto;
import jwt.auth.test.entity.User;
import jwt.auth.test.entity.VerificationToken;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends UserDetailsService {

    User save(UserDto userDto);

    String generateToken();

    VerificationToken createVerificationToken(User user);

    VerificationToken retrieveToken(String token);

    void activateAccount(VerificationToken user);

    void deleteAccount(Long userId);
}
