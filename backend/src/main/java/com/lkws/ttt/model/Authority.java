package com.lkws.ttt.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

/**
 * Specifies user grants.
 * Can be extended to allow for admin rights or different kinds of permissions.
 */
@Data
@AllArgsConstructor
public class Authority implements GrantedAuthority {

    public static final String USER = "USER";
    public static final Authority USER_AUTHORITY = new Authority(USER);

    private String authority;
}
