package com.lkws.ttt.datatransferobjects;

/**
 * Data transfer object to send JsonWebToken.
 */
public record TokenDTO(
        String token
) {
    public static TokenDTO of(String token) {
        return new TokenDTO(token);
    }
}
