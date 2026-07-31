package placement_OS.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import placement_OS.demo.entity.PlacementRequest;

import java.util.List;

@Repository
public interface PlacementRequestRepository extends JpaRepository<PlacementRequest, Long> {
    List<PlacementRequest> findByStatus(String status);
}