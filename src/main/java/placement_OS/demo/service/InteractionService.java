package placement_OS.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import placement_OS.demo.entity.Comment;
import placement_OS.demo.entity.Post;
import placement_OS.demo.entity.PostLike;
import placement_OS.demo.entity.User;
import placement_OS.demo.exception.ResourceNotFoundException;
import placement_OS.demo.repository.CommentRepository;
import placement_OS.demo.repository.PostLikeRepository;
import placement_OS.demo.repository.PostRepository;
import placement_OS.demo.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class InteractionService {

    private final PostLikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public InteractionService(PostLikeRepository likeRepository, CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository) {
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }
    // Check if user liked a post
    public boolean isPostLikedByUser(Long postId, String userEmail) {
        return userRepository.findByEmail(userEmail)
                .map(user -> likeRepository.existsByPostIdAndUserId(postId, user.getId()))
                .orElse(false);
    }

    // Get Like Count
    public long getLikeCount(Long postId) {
        return likeRepository.countByPostId(postId);
    }
    // Toggle Like (Like/Unlike)
    @Transactional
    public String toggleLike(Long postId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        Optional<PostLike> existingLike = likeRepository.findByPostIdAndUserId(postId, user.getId());

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            return "Unliked post successfully";
        } else {
            PostLike like = new PostLike();
            like.setPost(post);
            like.setUser(user);
            likeRepository.save(like);
            return "Liked post successfully";
        }
    }

    // Add Comment
    public Comment addComment(Long postId, String content, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(content);

        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByPost(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId);
    }

    // Author-Only Delete Comment Security
    public void deleteComment(Long commentId, String userEmail) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (!comment.getUser().getEmail().equals(userEmail)) {
            throw new IllegalStateException("Unauthorized: Only the comment author can delete it!");
        }

        commentRepository.delete(comment);
    }
}