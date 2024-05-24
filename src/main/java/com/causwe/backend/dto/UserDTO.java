package com.causwe.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private Long id;
    private String username;
    private String password;
    private Role role;

    public enum Role {
        ADMIN, PL, DEV, TESTER
    }

    public UserDTO() {}
}