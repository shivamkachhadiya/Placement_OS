package placement_OS.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import placement_OS.demo.entity.PostLike;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    // Check if user has liked the post
    boolean existsByPostIdAndUserId(Long postId, Long userId);

    // Find specific like entry for toggle/delete
    Optional<PostLike> findByPostIdAndUserId(Long postId, Long userId);

    // Count total likes on a post
    long countByPostId(Long postId);
}