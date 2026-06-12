package com.dev.doable.controller;

import com.dev.doable.dto.GoalDto;
import com.dev.doable.service.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/goals")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GoalController {

    private final GoalService goalService;

    @GetMapping
    public ResponseEntity<List<GoalDto>> findAll() {
        return ResponseEntity.ok(goalService.findAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<GoalDto>> findActive() {
        return ResponseEntity.ok(goalService.findActive());
    }

    @PostMapping
    public ResponseEntity<GoalDto> create(@RequestBody GoalDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(goalService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoalDto> update(@PathVariable Long id, @RequestBody GoalDto dto) {
        return ResponseEntity.ok(goalService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        goalService.delete(id);
        return ResponseEntity.noContent().build();
    }
}