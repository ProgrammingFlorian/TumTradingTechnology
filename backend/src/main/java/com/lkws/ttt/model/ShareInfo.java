package com.lkws.ttt.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "share_info")
public class ShareInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotBlank(message = "isin not found")
    @Column(name = "isin")
    private String isin;

    @NotBlank(message = "symbol not found")
    @Column(name = "symbol")
    private String symbol;

    @NotNull
    @Column(name = "current_price")
    private double currentPrice;

    @Column(name = "price_change")
    private double priceChange;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "currency")
    private String currency;

    @Column(name = "exchange")
    private String exchange;

    @Column(name = "dividend")
    private double dividend;

    @Column(name = "market_cap")
    private long marketCap;

    @Column(name = "sector")
    private String sector;

    @Column(name = "website")
    private String website;

    public ShareInfo(String isin, String symbol, double currentPrice, double priceChange, String companyName, String currency,
                     String exchange, double dividend, long marketCap, String sector, String website) {
        this.isin = isin;
        this.symbol = symbol;
        this.currentPrice = currentPrice;
        this.priceChange = priceChange;
        this.companyName = companyName;
        this.currency = currency;
        this.exchange = exchange;
        this.dividend = dividend;
        this.marketCap = marketCap;
        this.sector = sector;
        this.website = website;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShareInfo shareInfo = (ShareInfo) o;
        return id == shareInfo.id && Double.compare(shareInfo.currentPrice, currentPrice) == 0 && Double.compare(shareInfo.priceChange, priceChange) == 0 && Double.compare(shareInfo.dividend, dividend) == 0 && marketCap == shareInfo.marketCap && Objects.equals(isin, shareInfo.isin) && Objects.equals(symbol, shareInfo.symbol) && Objects.equals(companyName, shareInfo.companyName) && Objects.equals(currency, shareInfo.currency) && Objects.equals(exchange, shareInfo.exchange) && Objects.equals(sector, shareInfo.sector) && Objects.equals(website, shareInfo.website);
    }
}