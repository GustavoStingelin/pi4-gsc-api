package com.gs.pi4.api.app.vo.user;

import java.io.Serializable;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @EqualsAndHashCode @Builder
public class UserAccountCredentialsVO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private String email;
    private String password;
}
