package placement_OS.demo.service;

import org.springframework.stereotype.Service;
import placement_OS.demo.entity.Post;
import placement_OS.demo.entity.User;
import placement_OS.demo.exception.ResourceNotFoundException;
import placement_OS.demo.repository.PostRepository;
import placement_OS.demo.repository.UserRepository;

import java.util.Arrays;
import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // Keywords to validate placement-related content
    private static final List<String> ALLOWED_KEYWORDS = Arrays.asList(
            "interview", "placement", "offer", "company", "round", "rounds", "dsa",
            "system design", "design", "resume", "selected", "rejected", "question", "questions",
            "salary", "ctc", "experience", "test", "coding", "honeywell", "microsoft", "google",
            "amazon", "meta", "apple", "tcs", "infosys", "wipro", "accenture", "good",
            "interactive", "round1", "round2", "hr", "technical", "managerial", "aptitude"
    );

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public Post createPost(Post post, String userEmail) {
        // Checking across Company Name + Title + Content (All 3 fields)
        String combinedText = (post.getCompanyName() + " " + post.getTitle() + " " + post.getContent()).toLowerCase();

        boolean isRelevant = ALLOWED_KEYWORDS.stream().anyMatch(combinedText::contains);

        if (!isRelevant) {
            throw new IllegalArgumentException("Post rejected automatically: Content must be placement or career related!");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        post.setUser(user);
        return postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    // Get posts by specific user ID
    public List<Post> getPostsByUserId(Long userId) {
        return postRepository.findByUserId(userId);
    }

    // Author-only delete check
    public void deletePost(Long postId, String userEmail) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (!post.getUser().getEmail().equals(userEmail)) {
            throw new IllegalStateException("Unauthorized: You can only delete your own posts!");
        }

        postRepository.delete(post);
    }
    // Post edit / update functionality (Author-only restriction)
    public Post updatePost(Long postId, String companyName, String title, String content, String userEmail) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        // Security Check: Only author can edit their own post
        if (!post.getUser().getEmail().equals(userEmail)) {
            throw new IllegalArgumentException("Unauthorized: You can only edit your own posts!");
        }

        if (companyName != null && !companyName.trim().isEmpty()) {
            post.setCompanyName(companyName);
        }
        if (title != null && !title.trim().isEmpty()) {
            post.setTitle(title);
        }
        if (content != null && !content.trim().isEmpty()) {
            post.setContent(content);
        }

        return postRepository.save(post);
    }
}