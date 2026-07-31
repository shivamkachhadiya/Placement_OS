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

        // Default role
        user.setRole("STUDENT");

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

        return response;
    }
}