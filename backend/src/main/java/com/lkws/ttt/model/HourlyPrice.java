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
@Table(name = "hourlyPrice")
public class HourlyPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull(message = "shareId not found")
    @Column(name = "shareId")
    private long shareId;

    @Column(name = "date_time")
    private String dateTime;

    @NotNull
    @Column(name = "price")
    private double price;

    public HourlyPrice(long shareId, LocalDateTime dateTime, double price) {
        this.shareId = shareId;
        this.dateTime = convertLDTToString(dateTime);
        this.price = price;
    }

    private LocalDateTime convertStringToLDT(String dateTime)
    {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        return LocalDateTime.parse(dateTime, fmt);
    }

    private String convertLDTToString(LocalDateTime dateTime)
    {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        return dateTime.format(fmt);
    }

    public LocalDateTime getLDT()
    {
        return convertStringToLDT(getDateTime());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HourlyPrice that = (HourlyPrice) o;
        return id == that.id && shareId == that.shareId && Double.compare(that.price, price) == 0 && Objects.equals(dateTime, that.dateTime);
    }
}