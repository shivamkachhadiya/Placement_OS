package placement_OS.demo.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import placement_OS.demo.dto.CompanyRequestDTO;
import placement_OS.demo.dto.CompanyResponseDTO;
import placement_OS.demo.response.ApiResponse;
import placement_OS.demo.service.CompanyService;

import java.util.List;

@RestController
@RequestMapping("/companies")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @PostMapping
    public ResponseEntity<ApiResponse<CompanyResponseDTO>> saveCompany(
            @Valid @RequestBody CompanyRequestDTO request){

        CompanyResponseDTO response = companyService.saveCompany(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Company Created Successfully",
                        response
                )
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CompanyResponseDTO>>> getAllCompanies(){

        List<CompanyResponseDTO> response = companyService.getAllCompanies();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Companies Fetched Successfully",
                        response
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CompanyResponseDTO>> getCompanyById(
            @PathVariable Long id){

        CompanyResponseDTO response = companyService.getCompanyById(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Company Found",
                        response
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CompanyResponseDTO>> updateCompany(
            @PathVariable Long id,
            @Valid @RequestBody CompanyRequestDTO request){

        CompanyResponseDTO response =
                companyService.updateCompany(id, request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Company Updated Successfully",
                        response
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCompany(
            @PathVariable Long id){

        companyService.deleteCompany(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Company Deleted Successfully",
                        null
                )
        );
    }

}