package com.lkws.ttt.datatransferobjects;

import javax.validation.constraints.NotBlank;

/**
 * Data transfer object to be used for login.
 */
public record LoginDTO(
        @NotBlank String username,
        @NotBlank String password
) {
}
