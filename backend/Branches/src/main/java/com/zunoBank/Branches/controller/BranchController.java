package com.zunoBank.Branches.controller;

import com.zunoBank.Branches.dto.BranchDto;
import com.zunoBank.Branches.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ResponseEntity<BranchDto> create(@RequestBody BranchDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(branchService.createBranch(dto));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','BRANCH_MANAGER')")
    public ResponseEntity<List<BranchDto>> getAll() {
        return ResponseEntity.ok(branchService.getAllBranches());
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        branchService.deactivateBranch(id);
        return ResponseEntity.noContent().build();
    }
}