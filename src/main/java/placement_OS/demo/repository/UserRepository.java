package placement_OS.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import placement_OS.demo.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

}