package placement_OS.demo.mapper;

import placement_OS.demo.dto.UserRequestDTO;
import placement_OS.demo.dto.UserResponseDTO;
import placement_OS.demo.entity.User;

public class UserMapper {

    public static User toEntity(UserRequestDTO request) {

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setBranch(request.getBranch());
        user.setBatch(request.getBatch());

        // Dynamic role checking: Request mein role diya hai toh wo use karo, varna default STUDENT
        if (request.getRole() != null && !request.getRole().trim().isEmpty()) {
            user.setRole(request.getRole().toUpperCase());
        } else {
            user.setRole("STUDENT");
        }

        // Default placement status will be false via entity standard initialization
        user.setPlaced(false);

        return user;
    }

    public static UserResponseDTO toResponse(User user) {

        UserResponseDTO response = new UserResponseDTO();

        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setBranch(user.getBranch());
        response.setBatch(user.getBatch());
        response.setRole(user.getRole());

        // Placement status map ho gaya yahan
        response.setPlaced(user.isPlaced());

        return response;
    }
}