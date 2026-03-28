package com.dev.doable.controller;

import com.dev.doable.dto.LoginDto;
import com.dev.doable.dto.RegisterDto;
import com.dev.doable.dto.UserDto;
import com.dev.doable.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody RegisterDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDto dto) {
        String token = userService.login(dto);
        return ResponseEntity.ok(Map.of("token", token));
    }
}