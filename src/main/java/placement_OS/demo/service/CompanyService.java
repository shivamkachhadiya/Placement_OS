package placement_OS.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import placement_OS.demo.dto.CompanyRequestDTO;
import placement_OS.demo.dto.CompanyResponseDTO;
import placement_OS.demo.entity.Company;
import placement_OS.demo.exception.DuplicateResourceException;
import placement_OS.demo.exception.ResourceNotFoundException;
import placement_OS.demo.mapper.CompanyMapper;
import placement_OS.demo.repository.CompanyRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    public CompanyResponseDTO saveCompany(CompanyRequestDTO request){

        if(companyRepository.existsByCompanyName(request.getCompanyName())){
            throw new DuplicateResourceException("Company already exists");
        }

        Company company = CompanyMapper.toEntity(request);

        Company savedCompany = companyRepository.save(company);

        return CompanyMapper.toResponse(savedCompany);

    }

    public List<CompanyResponseDTO> getAllCompanies(){

        List<Company> companies = companyRepository.findAll();

        List<CompanyResponseDTO> response = new ArrayList<>();

        for(Company company : companies){

            response.add(CompanyMapper.toResponse(company));

        }

        return response;

    }

    public CompanyResponseDTO getCompanyById(Long id){

        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company Not Found"));

        return CompanyMapper.toResponse(company);

    }

    public CompanyResponseDTO updateCompany(Long id, CompanyRequestDTO request){

        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company Not Found"));

        company.setCompanyName(request.getCompanyName());
        company.setRole(request.getRole());
        company.setCtc(request.getCtc());
        company.setLocation(request.getLocation());
        company.setJobType(request.getJobType());
        company.setBatch(request.getBatch());

        Company updatedCompany = companyRepository.save(company);

        return CompanyMapper.toResponse(updatedCompany);

    }

    public String deleteCompany(Long id){

        if(!companyRepository.existsById(id)){
            throw new ResourceNotFoundException("Company Not Found");
        }

        companyRepository.deleteById(id);

        return "Company Deleted Successfully";

    }

}