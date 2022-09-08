package com.lkws.ttt.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "portfolio_share")
public class PortfolioShare {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull(message = "shareId not found")
    @Column(name = "share_id")
    private long shareId;

    @NotNull(message = "userId not found")
    @Column(name = "user_id")
    private long userId;

    @NotBlank(message = "date not found")
    @Column(name = "date_time")
    private String dateTime;

    @NotNull(message = "price not found")
    @Column(name = "price")
    private double price;

    @NotNull(message = "quantity not found")
    @Column(name = "quantity")
    private long quantity;

    public enum shareState {BOUGHT, SOLD}

    @NotNull(message = "shareOrderType not found")
    @Column(name = "order_type")
    private shareState orderType;

    public PortfolioShare(long shareId, long userId, LocalDateTime date, double price, long quantity, String orderType) {
        this.shareId = shareId;
        this.userId = userId;
        this.dateTime = convertLDTToString(date);
        this.price = price;
        this.quantity = quantity;
        this.orderType = shareState.valueOf(orderType);
    }

    public PortfolioShare(long shareId, long userId, String date, double price, long quantity, String orderType) {
        this.shareId = shareId;
        this.userId = userId;
        this.dateTime = date;
        this.price = price;
        this.quantity = quantity;
        this.orderType = shareState.valueOf(orderType);
    }

    private LocalDateTime convertStringToLDT(String dateTime) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        return LocalDateTime.parse(dateTime, fmt);
    }

    private String convertLDTToString(LocalDateTime dateTime) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        return dateTime.format(fmt);
    }

    public LocalDateTime getLDT() {
        return convertStringToLDT(getDateTime());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortfolioShare that = (PortfolioShare) o;
        return id == that.id && shareId == that.shareId && userId == that.userId && Double.compare(that.price, price) == 0 && quantity == that.quantity && Objects.equals(dateTime, that.dateTime) && orderType == that.orderType;
    }
}