package com.lkws.ttt.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "intradayPrice")
public class IntradayPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull(message = "shareId not found")
    @Column(name = "shareId")
    private long shareId;

    @NotNull()
    @Column(name = "date_time")
    private String date;

    @NotNull()
    @Column(name = "price")
    private double price;

    public IntradayPrice(long shareID, LocalDateTime date, double price) {
        this.shareId = shareID;
        this.date = convertLDTToString(date);
        this.price = price;
    }

    private String convertLDTToString(LocalDateTime dateTime) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy.MM.dd hh:mm:ss");
        return dateTime.format(fmt);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntradayPrice that = (IntradayPrice) o;
        return id == that.id && shareId == that.shareId && Double.compare(that.price, price) == 0 && Objects.equals(date, that.date);
    }
}
