package com.lkws.ttt.testvalues;

import com.lkws.ttt.datatransferobjects.PortfolioShareDTO;
import com.lkws.ttt.model.PortfolioShare;
import com.lkws.ttt.model.ShareInfo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class PortfolioTestValues {

    public static PortfolioShareDTO portfolioShareToBuy1 = new PortfolioShareDTO(1234,
            4321,
            LocalDateTime.of(LocalDate.of(1, 1, 1), LocalTime.of(1, 1)),
            100,
            5,
            "BOUGHT");

    public static PortfolioShare boughtPortfolioShare1 = new PortfolioShare(
            1234,
            4321,
            LocalDateTime.of(LocalDate.of(1, 1, 1), LocalTime.of(1, 1)),
            100,
            5,
            "BOUGHT");

    public static PortfolioShareDTO invalidPortfolioShareToBuy1 = new PortfolioShareDTO(1234,
            4321,
            LocalDateTime.of(LocalDate.of(1, 1, 1), LocalTime.of(1, 1)),
            100,
            -5,
            "BOUGHT");

    public static PortfolioShareDTO invalidPortfolioShareToBuy2 = new PortfolioShareDTO(1234,
            4321,
            LocalDateTime.of(LocalDate.of(1, 1, 1), LocalTime.of(1, 1)),
            100,
            5,
            "SOLD");

    public static PortfolioShareDTO portfolioShareDTOtoSell1 = new PortfolioShareDTO(1234,
            4321,
            LocalDateTime.of(LocalDate.of(1, 1, 1), LocalTime.of(1, 1)),
            100,
            5,
            "SOLD");

    public static PortfolioShare soldPortfolioShare1 = new PortfolioShare(
            1234,
            4321,
            LocalDateTime.of(LocalDate.of(1, 1, 1), LocalTime.of(1, 1)),
            100,
            5,
            "SOLD");

    public static ShareInfo dummyShareInfo = new ShareInfo();
}
