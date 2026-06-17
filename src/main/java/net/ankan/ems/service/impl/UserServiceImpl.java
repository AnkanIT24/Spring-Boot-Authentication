package net.ankan.ems.service.impl;

import net.ankan.ems.dto.UpdateNameRequest;
import net.ankan.ems.dto.UserResponseDto;
import net.ankan.ems.entity.User;
import net.ankan.ems.exception.ResourceNotFoundException;
import net.ankan.ems.mapper.UserMapper;
import net.ankan.ems.repository.UserRepository;
import net.ankan.ems.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserResponseDto getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Logged-in user not found with email: " + currentUserEmail));

        return UserMapper.toUserResponseDto(user);
    }

    @Override
    public UserResponseDto updateCurrentUserName(UpdateNameRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Logged-in user not found with email: " + currentUserEmail));

        user.setFullName(request.getFullName());
        userRepository.save(user);

        return UserMapper.toUserResponseDto(user);
    }
}