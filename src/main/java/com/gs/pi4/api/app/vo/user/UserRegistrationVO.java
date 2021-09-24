package com.gs.pi4.api.app.vo.user;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @EqualsAndHashCode @Builder @NoArgsConstructor @AllArgsConstructor
public class UserRegistrationVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private char gender;
    private Date birthday;
    
}