package placement_OS.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import placement_OS.demo.entity.Post;
import placement_OS.demo.response.ApiResponse;
import placement_OS.demo.service.PostService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Post>> updatePost(@PathVariable Long id, @RequestBody Map<String, String> body, Authentication authentication) {
        Post post = postService.updatePost(id, body.get("companyName"), body.get("title"), body.get("content"), authentication.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Post updated successfully", post));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Post>> createPost(@RequestBody Post post, Authentication authentication) {
        Post created = postService.createPost(post, authentication.getName());
        return ResponseEntity.status(201).body(new ApiResponse<>(true, "Post created successfully", created));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Post>>> getAllPosts() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Posts fetched", postService.getAllPosts()));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Post>>> getPostsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "User posts fetched", postService.getPostsByUserId(userId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deletePost(@PathVariable Long id, Authentication authentication) {
        postService.deletePost(id, authentication.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Post deleted successfully", null));
    }
}