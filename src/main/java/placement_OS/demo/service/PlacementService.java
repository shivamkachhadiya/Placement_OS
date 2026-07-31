package placement_OS.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import placement_OS.demo.entity.PlacementRequest;
import placement_OS.demo.entity.Post;
import placement_OS.demo.entity.User;
import placement_OS.demo.exception.ResourceNotFoundException;
import placement_OS.demo.repository.PlacementRequestRepository;
import placement_OS.demo.repository.PostRepository;
import placement_OS.demo.repository.UserRepository;

import java.util.List;

@Service
public class PlacementService {

    private final PlacementRequestRepository requestRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PlacementService(PlacementRequestRepository requestRepository,
                            PostRepository postRepository,
                            UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    // 1. Student sends request: "Please mark me as placed for this post"
// 1. Student sends request: "Please mark me as placed for this post"
    public PlacementRequest createRequest(Long postId, String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + studentEmail));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));

        PlacementRequest request = new PlacementRequest();
        request.setStudent(student);
        request.setPost(post);

        // Safety check: Agar post.getCompanyName() hai toh use karo, nahi toh fallback
        request.setCompanyName(post.getCompanyName());
        request.setStatus("PENDING");

        return requestRepository.save(request);
    }

    // 2. Admin views all pending requests
    public List<PlacementRequest> getPendingRequests() {
        return requestRepository.findByStatus("PENDING");
    }

    // 3. Admin Approves or Rejects request
    @Transactional
    public String resolveRequest(Long requestId, boolean approve) {
        PlacementRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with ID: " + requestId));

        if (approve) {
            request.setStatus("APPROVED");

            // Mark user as PLACED
            User student = request.getStudent();
            student.setPlaced(true);
            userRepository.save(student);

            return "Student placement status approved and updated to PLACED!";
        } else {
            request.setStatus("REJECTED");
            return "Placement request rejected.";
        }
    }
}