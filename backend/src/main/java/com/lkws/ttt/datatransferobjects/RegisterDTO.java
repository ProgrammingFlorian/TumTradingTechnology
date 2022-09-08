package com.lkws.ttt.datatransferobjects;

import javax.validation.constraints.NotBlank;

/**
 * Data transfer object to be used to register a new user.
 */
public record RegisterDTO(
        // TODO: Add more properties (firstname, lastname) and unify email/username
        @NotBlank String username,
        @NotBlank String password
) {
}