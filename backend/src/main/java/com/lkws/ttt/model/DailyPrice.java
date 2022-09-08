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
@Table(name = "dailyPrice")
public class DailyPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull(message = "shareId not found")
    @Column(name = "shareId")
    private long shareId;

    @Column(name = "date")
    private String date;

    @NotNull
    @Column(name = "price")
    private double price;

    public DailyPrice(long shareId, LocalDateTime date, double price) {
        this.shareId = shareId;
        this.date = convertLDTToString(date);
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
        return convertStringToLDT(getDate());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DailyPrice that = (DailyPrice) o;
        return id == that.id && shareId == that.shareId && Double.compare(that.price, price) == 0 && Objects.equals(date, that.date);
    }
}