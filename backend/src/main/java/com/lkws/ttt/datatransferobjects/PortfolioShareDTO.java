package com.lkws.ttt.datatransferobjects;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * Data transfer object to be used for interactions with portfolio.
 */
public record PortfolioShareDTO(
        @NotBlank long shareId,
        @NotBlank long userId,
        @NotBlank LocalDateTime date_time,
        @NotBlank double price,
        @NotBlank long quantity,
        @NotBlank String orderType
) {
    public static PortfolioShareDTO of(long shareId, long userId, LocalDateTime localDateTime, double price, long quantity, String orderType) {
        return new PortfolioShareDTO(shareId, userId, localDateTime, price, quantity, orderType);
    }
}
