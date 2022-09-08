package com.lkws.ttt.datatransferobjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lkws.ttt.model.User;

import javax.validation.constraints.NotBlank;

/**
 * Data transfer object for user model.
 *
 * Password should not be included for security reasons.
 */
public record UserDTO(
        @JsonProperty("username") String username,
        @NotBlank double cash
) {

    public static UserDTO of(User source) {
        return new UserDTO(source.getUsername(), source.getCash());
    }

}
