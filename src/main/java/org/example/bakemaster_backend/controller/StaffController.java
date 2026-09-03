package org.example.bakemaster_backend.controller;

import jakarta.validation.Valid;
import org.example.bakemaster_backend.dto.StaffDto;
import org.example.bakemaster_backend.entity.Staff;
import org.example.bakemaster_backend.service.StaffService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffController {
    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping
    public List<Staff> getAllStaff() {
        return staffService.getAllStaff();
    }

    @GetMapping("/active")
    public List<Staff> getActiveStaff() {
        return staffService.getActiveStaff();
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    public Staff addStaff(@Valid @RequestBody StaffDto dto) {
        return staffService.addStaff(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    public Staff updateStaff(@PathVariable Long id, @Valid @RequestBody StaffDto dto) {
        return staffService.updateStaff(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_ADMIN')")
    public void deleteStaff(@PathVariable Long id) {
        staffService.deleteStaff(id);
    }
}
