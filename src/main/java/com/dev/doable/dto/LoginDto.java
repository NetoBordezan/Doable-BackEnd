package com.dev.doable.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {

    private String email;
    private String password;
}