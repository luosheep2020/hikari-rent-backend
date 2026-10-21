package com.hikari.rent.auth.service;

import com.hikari.rent.auth.repository.AuthAccountRepository;
import com.hikari.rent.auth.repository.model.AuthAccountRow;
import com.hikari.rent.auth.vo.AuthUserVO;
import com.hikari.rent.file.service.FileUrlService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthIdentityService {

  @Resource
  private AuthAccountRepository authAccountRepository;

  @Resource
  private FileUrlService fileUrlService;

  public AuthUserVO toUserVO(AuthAccountRow account) {
    List<String> roles =
      authAccountRepository.findActiveRoles(
        account.getId()
      );

    String avatar = account.getAvatarStorageKey() == null
      ? null
      : fileUrlService.getPublicUrl(
      account.getAvatarStorageKey()
    );

    return new AuthUserVO(
      account.getId().toString(),
      account.getEmail(),
      account.getEmail(),
      account.getNickname(),
      account.getPhone() == null
        ? ""
        : account.getPhone(),
      avatar,
      account.getCompanyId() == null
        ? null
        : account.getCompanyId().toString(),
      List.copyOf(roles)
    );
  }
}
