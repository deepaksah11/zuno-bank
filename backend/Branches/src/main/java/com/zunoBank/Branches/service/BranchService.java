package com.zunoBank.Branches.service;

import com.zunoBank.Branches.dto.BranchDto;
import com.zunoBank.Branches.entity.Branch;
import com.zunoBank.Branches.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchService {

    private final BranchRepository branchRepository;

    public BranchDto createBranch(BranchDto dto) {
        Branch branch = Branch.builder()
                .branchCode(dto.getBranchCode())
                .branchName(dto.getBranchName())
                .city(dto.getCity())
                .state(dto.getState())
                .active(true)
                .build();

        branchRepository.save(branch);
        return map(branch);
    }

    public List<BranchDto> getAllBranches() {
        return branchRepository.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    public void deactivateBranch(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        branch.setActive(false);
        branchRepository.save(branch);
    }

    private BranchDto map(Branch b) {
        BranchDto dto = new BranchDto();
        dto.setId(b.getId());
        dto.setBranchCode(b.getBranchCode());
        dto.setBranchName(b.getBranchName());
        dto.setCity(b.getCity());
        dto.setState(b.getState());
        dto.setActive(b.isActive());
        return dto;
    }
}