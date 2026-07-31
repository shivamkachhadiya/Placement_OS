package placement_OS.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import placement_OS.demo.dto.LoginRequestDTO;
import placement_OS.demo.dto.LoginResponseDTO;
import placement_OS.demo.dto.UserRequestDTO;
import placement_OS.demo.entity.User;
import placement_OS.demo.repository.UserRepository;
import placement_OS.demo.response.ApiResponse;
import placement_OS.demo.service.AuthService;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(@Valid @RequestBody UserRequestDTO dto) {
        String msg = authService.register(dto);
        User user = userRepository.findByEmail(dto.getEmail()).orElse(null);

        Map<String, Object> data = new HashMap<>();
        data.put("message", msg);
        data.put("user", user);

        return ResponseEntity.ok(new ApiResponse<>(true, "Registration successful", data));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@Valid @RequestBody LoginRequestDTO dto) {
        LoginResponseDTO loginResponse = authService.login(dto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", loginResponse));
    }
}