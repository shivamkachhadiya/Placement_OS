package placement_OS.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import placement_OS.demo.entity.Comment;
import placement_OS.demo.response.ApiResponse;
import placement_OS.demo.service.InteractionService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/posts")
public class InteractionController {

    private final InteractionService interactionService;

    public InteractionController(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    // Toggle Like + Return Updated Count and Status
    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleLike(@PathVariable Long postId, Authentication authentication) {
        String msg = interactionService.toggleLike(postId, authentication.getName());
        long count = interactionService.getLikeCount(postId);
        boolean liked = interactionService.isPostLikedByUser(postId, authentication.getName());

        Map<String, Object> data = new HashMap<>();
        data.put("message", msg);
        data.put("likesCount", count);
        data.put("isLiked", liked);

        return ResponseEntity.ok(new ApiResponse<>(true, msg, data));
    }

    // Get Like Details for Post
    @GetMapping("/{postId}/like-details")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getLikeDetails(@PathVariable Long postId, Authentication authentication) {
        long count = interactionService.getLikeCount(postId);
        boolean liked = (authentication != null && authentication.isAuthenticated())
                ? interactionService.isPostLikedByUser(postId, authentication.getName())
                : false;

        Map<String, Object> data = new HashMap<>();
        data.put("likesCount", count);
        data.put("isLiked", liked);

        return ResponseEntity.ok(new ApiResponse<>(true, "Like details retrieved", data));
    }

    // Add Comment
    @PostMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<Comment>> addComment(
            @PathVariable Long postId,
            @RequestBody Map<String, String> body,
            Authentication authentication) {

        String content = body.get("content");
        Comment comment = interactionService.addComment(postId, content, authentication.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Comment added successfully", comment));
    }

    // Get Post Comments
    @GetMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<List<Comment>>> getComments(@PathVariable Long postId) {
        List<Comment> comments = interactionService.getCommentsByPost(postId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Comments retrieved", comments));
    }

    // Delete Comment
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<String>> deleteComment(@PathVariable Long commentId, Authentication authentication) {
        interactionService.deleteComment(commentId, authentication.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Comment deleted successfully", null));
    }
}