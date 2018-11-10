package jwt.auth.test.service.impl;

import jwt.auth.test.dto.UserDto;
import jwt.auth.test.entity.User;
import jwt.auth.test.entity.UserRole;
import jwt.auth.test.entity.VerificationToken;
import jwt.auth.test.exception.ApiException;
import jwt.auth.test.repository.UserRepository;
import jwt.auth.test.repository.UserRoleRepository;
import jwt.auth.test.repository.VerificationTokenRepository;
import jwt.auth.test.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static jwt.auth.test.utils.Constants.ROLE_USER;

@Slf4j
@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final VerificationTokenRepository verificationTokenRepository;

    @Override
    public User save(UserDto userDto) {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        UserRole defaultAuthority = userRoleRepository.findByRole(ROLE_USER)
                .orElseThrow(() -> new ApiException(
                        "No default authority provided", HttpStatus.INTERNAL_SERVER_ERROR));

        if (userRepository.findByEmail(userDto.getEmail())
                .isPresent()) {
            log.debug("User with email [{}] already exists ", userDto.getEmail());
            throw new ApiException("User already exists", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setEnabled(false);
        user.setAuthorities(Collections.singletonList(defaultAuthority));
        user.setAccountNonLocked(true);
        user.setAccountNonExpired(true);

        log.debug("saving user [{}]", user);
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public VerificationToken createVerificationToken(User user) {
        String token = generateToken();
        VerificationToken verificationToken =
                new VerificationToken(token, user);
        verificationTokenRepository.save(verificationToken);

        return verificationToken;
    }

    @Override
    public VerificationToken retrieveToken(String token) {
        Optional<VerificationToken> tokenOptional =
                verificationTokenRepository.findByToken(token);

        return tokenOptional.orElseThrow(() -> new HttpMessageNotReadableException("No token was found"));
    }

    @Transactional
    @Override
    public void deleteAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        user.setEnabled(false);
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void activateAccount(VerificationToken token) {
        User user = token.getUser();
        user.setEnabled(true);
        verificationTokenRepository.delete(token);
        userRepository.save(user);
    }
}
