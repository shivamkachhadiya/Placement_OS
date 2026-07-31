package placement_OS.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import placement_OS.demo.dto.UserRequestDTO;
import placement_OS.demo.dto.UserResponseDTO;
import placement_OS.demo.entity.User;
import placement_OS.demo.exception.ResourceNotFoundException;
import placement_OS.demo.mapper.UserMapper;
import placement_OS.demo.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ========================= REGISTER USER =========================

    public UserResponseDTO saveUser(UserRequestDTO requestDTO) {

        if (userRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();

        user.setName(requestDTO.getName());
        user.setEmail(requestDTO.getEmail());
        user.setBranch(requestDTO.getBranch());
        user.setBatch(requestDTO.getBatch());

        // Encrypt Password
        user.setPassword(
                passwordEncoder.encode(requestDTO.getPassword())
        );

        // Default Role
        user.setRole("STUDENT");

        User savedUser = userRepository.save(user);

        return UserMapper.toResponse(savedUser);
    }

    // ========================= GET ALL USERS =========================

    public List<UserResponseDTO> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ========================= GET USER BY ID =========================

    public UserResponseDTO getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User Not Found"));

        return UserMapper.toResponse(user);
    }

    // ========================= UPDATE USER =========================

    public UserResponseDTO updateUser(Long id, User userRequest) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User Not Found"));

        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setBranch(userRequest.getBranch());
        user.setBatch(userRequest.getBatch());

        if (userRequest.getPassword() != null &&
                !userRequest.getPassword().isBlank()) {

            user.setPassword(
                    passwordEncoder.encode(userRequest.getPassword())
            );
        }

        User updatedUser = userRepository.save(user);

        return UserMapper.toResponse(updatedUser);
    }

    // ========================= DELETE USER =========================

    public String deleteUser(Long id) {

        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User Not Found");
        }

        userRepository.deleteById(id);

        return "User Deleted Successfully";
    }
}