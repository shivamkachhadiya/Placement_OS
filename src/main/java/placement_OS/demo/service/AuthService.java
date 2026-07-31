package placement_OS.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import placement_OS.demo.dto.LoginRequestDTO;
import placement_OS.demo.dto.LoginResponseDTO;
import placement_OS.demo.dto.UserRequestDTO;
import placement_OS.demo.entity.User;
import placement_OS.demo.exception.DuplicateResourceException;
import placement_OS.demo.exception.ResourceNotFoundException;
import placement_OS.demo.repository.UserRepository;
import placement_OS.demo.security.CustomUserDetails;
import placement_OS.demo.security.CustomUserDetailsService;
import placement_OS.demo.security.JwtService;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    public String register(UserRequestDTO request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setBranch(request.getBranch());
        user.setBatch(request.getBatch());
        user.setRole("STUDENT");

        userRepository.save(user);

        return "Registration Successful";
    }

    public LoginResponseDTO login(LoginRequestDTO request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        CustomUserDetails userDetails =
                (CustomUserDetails) customUserDetailsService
                        .loadUserByUsername(request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User Not Found"));

        String token = jwtService.generateToken(userDetails);

        return new LoginResponseDTO(
                token,
                "Login Successful",
                user.getRole(),
                user.getName()
        );
    }
}