package org.example.bakemaster_backend.controller;

import jakarta.validation.Valid;
import org.example.bakemaster_backend.dto.WasteLogDto;
import org.example.bakemaster_backend.entity.WasteLog;
import org.example.bakemaster_backend.service.WasteLogService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/waste-logs")
public class WasteLogController {

    private final WasteLogService wasteLogService;

    public WasteLogController(WasteLogService wasteLogService) {
        this.wasteLogService = wasteLogService;
    }

    @GetMapping
    public List<WasteLog> getAll() {
        return wasteLogService.getAll();
    }

    @GetMapping("/range")
    public List<WasteLog> getByRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        return wasteLogService.getByRange(start, end);
    }

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getAnalytics() {
        return ResponseEntity.ok(wasteLogService.getWasteAnalytics());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<WasteLog> create(@Valid @RequestBody WasteLogDto dto) {
        WasteLog created = wasteLogService.logWaste(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}
