package com.dev.doable.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {

    private String name;
    private String email;
    private String password;
}