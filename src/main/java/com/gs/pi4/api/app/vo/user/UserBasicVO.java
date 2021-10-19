package com.gs.pi4.api.app.vo.user;

import java.io.Serializable;

import com.gs.pi4.api.core.user.IUserBasic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @EqualsAndHashCode @Builder @NoArgsConstructor @AllArgsConstructor
public class UserBasicVO implements Serializable, IUserBasic {
    
    private static final long serialVersionUID = 1L;

    private String firstName;
    private Long profileImage;
    private String email;
}
