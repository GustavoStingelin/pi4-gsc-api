package com.gs.pi4.api.app.vo;

import java.io.Serializable;
import java.util.Date;

import com.gs.pi4.api.app.vo.user.UserVO;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @EqualsAndHashCode @Builder
public class TimestampLogVO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private Date createdAt;
    private UserVO createdBy;

    private Date changedAt;
    private UserVO changedBy;
    
    private Date deletedAt;
    private UserVO deletedBy;

}
