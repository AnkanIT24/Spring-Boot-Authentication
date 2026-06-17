package net.ankan.ems.service.impl;

import lombok.AllArgsConstructor;
import net.ankan.ems.dto.AuthResponse;
import net.ankan.ems.dto.ChangePasswordRequest;
import net.ankan.ems.dto.LoginRequest;
import net.ankan.ems.dto.RegisterRequest;
import net.ankan.ems.entity.Department;
import net.ankan.ems.entity.Employee;
import net.ankan.ems.entity.Role;
import net.ankan.ems.entity.User;
import net.ankan.ems.exception.EmailAlreadyExistsException;
import net.ankan.ems.exception.InvalidCredentialsException;
import net.ankan.ems.repository.DepartmentRepository;
import net.ankan.ems.repository.EmployeeRepository;
import net.ankan.ems.repository.UserRepository;
import net.ankan.ems.security.CustomUserDetailsService;
import net.ankan.ems.security.JwtUtil;
import net.ankan.ems.service.AuthService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "An account with email " + request.getEmail() + " already exists");
        }

        // Save user
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role role;
        try {
            role = Role.valueOf(request.getRole().trim().toUpperCase());
        } catch (Exception e) {
            role = Role.USER;
        }
        user.setRole(role);

        User savedUser = userRepository.save(user);

        // If USER role — auto-create employee record
        if (role == Role.USER) {
            String[] nameParts = request.getFullName().trim().split(" ", 2);
            String firstName = nameParts[0];
            String lastName = nameParts.length > 1 ? nameParts[1] : "";

            Department department = null;
            if (request.getDepartmentId() != null) {
                department = departmentRepository.findById(request.getDepartmentId())
                        .orElse(null);
            }

            Employee employee = new Employee();
            employee.setFirstName(firstName);
            employee.setLastName(lastName);
            employee.setEmail(savedUser.getEmail());
            employee.setDepartment(department);
            employee.setUserId(savedUser.getId());
            employeeRepository.save(employee);
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        return new AuthResponse(token, savedUser.getFullName(), savedUser.getEmail(), savedUser.getRole().name());
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        return new AuthResponse(token, user.getFullName(), user.getEmail(), user.getRole().name());
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}