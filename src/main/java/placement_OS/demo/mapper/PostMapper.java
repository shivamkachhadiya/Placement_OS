package placement_OS.demo.mapper;

import placement_OS.demo.dto.PostRequestDTO;
import placement_OS.demo.dto.PostResponseDTO;
import placement_OS.demo.entity.Post;
import placement_OS.demo.entity.User;

public class PostMapper {

    public static Post toEntity(PostRequestDTO request, User user) {
        Post post = new Post();
        post.setCompanyName(request.getCompanyName());
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setUser(user); // Associated user/student
        return post;
    }

    public static PostResponseDTO toResponse(Post post) {
        PostResponseDTO response = new PostResponseDTO();
        response.setId(post.getId());
        response.setUserId(post.getUser().getId());
        response.setStudentName(post.getUser().getName());
        response.setCompanyName(post.getCompanyName());
        response.setTitle(post.getTitle());
        response.setContent(post.getContent());
        response.setStudentPlaced(post.getUser().isPlaced()); // Feed par badge dikhane ke liye
        response.setCreatedAt(post.getCreatedAt());
        return response;
    }
}