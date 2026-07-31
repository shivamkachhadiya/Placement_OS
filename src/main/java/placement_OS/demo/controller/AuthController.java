package placement_OS.demo.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import placement_OS.demo.dto.LoginRequestDTO;
import placement_OS.demo.dto.LoginResponseDTO;
import placement_OS.demo.dto.UserRequestDTO;
import placement_OS.demo.response.ApiResponse;
import placement_OS.demo.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(
            @Valid @RequestBody UserRequestDTO request) {

        String response = authService.register(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        response,
                        null
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO request) {

        LoginResponseDTO response = authService.login(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Login Successful",
                        response
                )
        );
    }

}