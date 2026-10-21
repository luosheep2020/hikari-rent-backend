package com.hikari.rent.auth.service.impl;

import com.hikari.rent.auth.cache.LoginSessionCache;
import com.hikari.rent.auth.dto.LoginRequest;
import com.hikari.rent.auth.dto.RegisterRequest;
import com.hikari.rent.auth.exception.EmailAlreadyRegisteredException;
import com.hikari.rent.auth.model.AuthPrincipal;
import com.hikari.rent.auth.model.LoginSession;
import com.hikari.rent.auth.repository.AuthAccountRepository;
import com.hikari.rent.auth.repository.model.AuthAccountRow;
import com.hikari.rent.auth.repository.model.AuthRegisterRow;
import com.hikari.rent.auth.service.AuthIdentityService;
import com.hikari.rent.auth.service.AuthService;
import com.hikari.rent.auth.service.CurrentUserService;
import com.hikari.rent.auth.service.JwtTokenService;
import com.hikari.rent.auth.vo.AuthUserVO;
import com.hikari.rent.auth.vo.LoginVO;
import com.hikari.rent.file.service.FileUrlService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

  @Resource
  private AuthAccountRepository authAccountRepository;

  @Resource
  private PasswordEncoder passwordEncoder;

  @Resource
  private JwtTokenService jwtTokenService;
  @Resource
  private CurrentUserService currentUserService;

  @Resource
  private LoginSessionCache loginSessionCache;

  @Resource
  private FileUrlService fileUrlService;

  @Resource
  private AuthIdentityService authIdentityService;

  private String dummyPasswordHash;

  @PostConstruct
  public void initializeDummyPasswordHash() {
    dummyPasswordHash = passwordEncoder.encode(
      UUID.randomUUID().toString()
    );
  }

  @Override
  public LoginVO login(LoginRequest request) {
    String email = request.email()
      .trim()
      .toLowerCase(Locale.ROOT);

    AuthAccountRow account =
      authAccountRepository.findByEmail(email);

    String passwordHash = account == null
      ? dummyPasswordHash
      : account.getPasswordHash();

    boolean passwordMatches = matchesPassword(
      request.password(),
      passwordHash
    );

    if (
      !passwordMatches
        || account == null
        || !Integer.valueOf(1).equals(account.getStatus())
    ) {
      throw new BadCredentialsException(
        "Invalid email or password"
      );
    }

    AuthUserVO user =
      authIdentityService.toUserVO(account);

    JwtTokenService.IssuedToken token =
      jwtTokenService.issue(account.getId());

    LoginSession session = new LoginSession(
      account.getId().toString(),
      LoginSessionCache.fingerprint(
        token.accessToken()
      ),
      user,
      token.issuedAt(),
      token.expiresAt(),
      account.getPasswordChangedAt()
    );

    // Return the token only after the Redis session is saved.
    loginSessionCache.save(
      token.sessionId(),
      session
    );

    return new LoginVO(
      token.accessToken(),
      "Bearer",
      token.expiresAt(),
      user
    );
  }

  private boolean matchesPassword(
    String rawPassword,
    String storedHash
  ) {
    String hash = storedHash == null || storedHash.isBlank()
      ? dummyPasswordHash
      : storedHash;

    try {
      return passwordEncoder.matches(
        rawPassword,
        hash
      );
    } catch (IllegalArgumentException ex) {
      // An invalid stored hash must never authenticate.
      return false;
    }
  }
  @Override
  public AuthUserVO me() {
    return currentUserService
      .requirePrincipal()
      .user();
  }

  @Override
  public void logout() {
    AuthPrincipal principal =
      currentUserService.requirePrincipal();

    loginSessionCache.delete(
      principal.sessionId()
    );
  }

  @Override
  @Transactional
  public void register(RegisterRequest request) {
    String email = request.email()
      .trim()
      .toLowerCase(Locale.ROOT);

    if (authAccountRepository.findByEmail(email) != null) {
      throw new EmailAlreadyRegisteredException();
    }

    Long roleId =
      authAccountRepository.findUserRoleId();

    if (roleId == null) {
      throw new AuthenticationServiceException(
        "The default registration role is unavailable"
      );
    }

    AuthRegisterRow account = new AuthRegisterRow();

    account.setEmail(email);
    account.setNickname(request.nickname().trim());

    account.setPasswordHash(
      passwordEncoder.encode(
        request.password()
      )
    );

    int inserted;

    try {
      inserted = authAccountRepository.createAccount(
        account
      );
    } catch (DuplicateKeyException exception) {
      // The unique index also protects concurrent registrations.
      throw new EmailAlreadyRegisteredException();
    }

    if (inserted != 1 || account.getId() == null) {
      throw new AuthenticationServiceException(
        "Failed to create the account"
      );
    }

    int assigned = authAccountRepository.addUserRole(
      account.getId(),
      roleId
    );

    if (assigned != 1) {
      throw new AuthenticationServiceException(
        "Failed to assign the default registration role"
      );
    }
  }
}
