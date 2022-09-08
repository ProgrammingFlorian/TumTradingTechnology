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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "portfolio_value")
public class PortfolioValue {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull(message = "userId not found")
    @Column(name = "userId")
    private long userId;

    @NotBlank(message = "date not found")
    @Column(name = "date_time")
    private String dateTime;

    @NotNull(message = "portfolioValue not found")
    @Column(name = "portfolio_value")
    private double portfolioValue;


    public PortfolioValue(long userId, LocalDateTime date, double portfolioValue) {
        this.userId = userId;
        this.dateTime = convertLDTToString(date);
        this.portfolioValue = portfolioValue;
    }

    public PortfolioValue(long userId, String date, double portfolioValue) {
        this.userId = userId;
        this.dateTime = date;
        this.portfolioValue = portfolioValue;
    }

    private LocalDateTime convertStringToLDT(String dateTime) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy.MM.dd hh:mm:ss");
        return LocalDateTime.parse(dateTime, fmt);
    }

    private String convertLDTToString(LocalDateTime dateTime) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy.MM.dd hh:mm:ss");
        return dateTime.format(fmt);
    }

    public LocalDateTime getLDT() {
        return convertStringToLDT(getDateTime());
    }
}