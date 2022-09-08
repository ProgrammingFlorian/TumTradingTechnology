package com.lkws.ttt.datatransferobjects;

/**
 * Data transfer object for errors occurring during the request.
 */
public record ErrorDTO(
        String title,
        String message
) {
}
