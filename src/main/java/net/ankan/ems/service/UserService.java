package net.ankan.ems.service;

import net.ankan.ems.dto.UpdateNameRequest;
import net.ankan.ems.dto.UserResponseDto;

public interface UserService {

    UserResponseDto getCurrentUserProfile();

    UserResponseDto updateCurrentUserName(UpdateNameRequest request);
}