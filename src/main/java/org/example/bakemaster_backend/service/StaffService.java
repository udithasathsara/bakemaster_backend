package org.example.bakemaster_backend.service;

import org.example.bakemaster_backend.dto.StaffDto;
import org.example.bakemaster_backend.entity.Staff;
import org.example.bakemaster_backend.repository.StaffRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffService {
    private final StaffRepository staffRepo;

    public StaffService(StaffRepository staffRepo) {
        this.staffRepo = staffRepo;
    }

    public List<Staff> getAllStaff() {
        return staffRepo.findAll();
    }

    public List<Staff> getActiveStaff() {
        return staffRepo.findByActiveTrue();
    }

    public Staff addStaff(StaffDto dto) {
        Staff staff = new Staff();
        staff.setName(dto.getName());
        staff.setRole(dto.getRole());
        staff.setPhone(dto.getPhone());
        staff.setEmail(dto.getEmail());
        staff.setShiftStart(dto.getShiftStart());
        staff.setShiftEnd(dto.getShiftEnd());
        staff.setActive(dto.isActive());
        return staffRepo.save(staff);
    }

    public Staff updateStaff(Long id, StaffDto dto) {
        Staff staff = staffRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        staff.setName(dto.getName());
        staff.setRole(dto.getRole());
        staff.setPhone(dto.getPhone());
        staff.setEmail(dto.getEmail());
        staff.setShiftStart(dto.getShiftStart());
        staff.setShiftEnd(dto.getShiftEnd());
        staff.setActive(dto.isActive());
        return staffRepo.save(staff);
    }

    public void deleteStaff(Long id) {
        staffRepo.deleteById(id);
    }
}
