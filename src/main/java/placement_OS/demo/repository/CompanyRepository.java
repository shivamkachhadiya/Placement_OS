package placement_OS.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import placement_OS.demo.entity.Company;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByCompanyName(String companyName);

    boolean existsByCompanyName(String companyName);

}