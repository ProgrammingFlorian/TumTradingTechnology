package com.lkws.ttt.services;


import com.lkws.ttt.datatransferobjects.PortfolioShareDTO;
import com.lkws.ttt.model.*;
import com.lkws.ttt.repository.PortfolioShareRepository;
import com.lkws.ttt.repository.PortfolioValueRepository;
import com.lkws.ttt.repository.ShareInfoRepository;
import com.lkws.ttt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioShareRepository portfolioShareRepository;
    private final PortfolioValueRepository portfolioValueRepository;
    private final UserRepository userRepository;
    private final ShareInfoRepository shareInfoRepository;

    /**
     * Returns all shares in portfolio in ShareInfo format.
     *
     * @return a list containing shares in ShareInfo format
     */
    public List<ShareInfo> getSharesInPortfolio(long userId) {
        return getAllPortfolioShares(userId).stream()
                .map(PortfolioShare::getShareId)
                .distinct()
                .map(x -> shareInfoRepository.findById(x).orElseThrow())
                .toList();
    }

    /**
     * Returns all Portfolio-Value data points for the Portfolio-Performance-Graph of a User
     *
     * @param timeSpan -> receive data points based on the given time span
     */
    public List<PortfolioValue> getPortfolioValueDataPoints(long userId, TimeSpan timeSpan) {
        return switch (timeSpan) {
            case DAY -> getDataPointsPortfolioDay(userId);
            case WEEK -> getDataPointsPortfolioWeek(userId);
            case MONTH -> getDataPointsPortfolioMonth(userId);
            case YEAR -> getDataPointsPortfolioYear(userId);
            case MAX -> getDataPointsPortfolioMax(userId);
        };
    }

    /**
     * Automatically saves all current "live = 60 sec" portfolio values for all users registeres
     */
    @Scheduled(fixedRate = 60000)
    public void livePortfolioValue() {
        if (!ShareService.marketIsOpen()) {
            return;
        }
        var u = userRepository.findAll();
        List<User> users = new ArrayList<>();
        u.forEach(users::add);

        List<PortfolioValue> portfolioValues = new ArrayList<>();

        for (User user : users) {
            double stockValues = getSharesInPortfolio(user.getId()).stream()
                    .mapToDouble(s -> getPortfolioQuantityByShareId(s.getId(), user.getId()) * s.getCurrentPrice())
                    .sum();
            double portfolioValue = stockValues + user.getCash();

            portfolioValues.add(new PortfolioValue(user.getId(), LocalDateTime.now(ZoneId.of("America/New_York")), portfolioValue));
        }

        portfolioValueRepository.saveAll(portfolioValues);
    }

    /**
     * Calculates the amount of a certain share in the portfolio.
     *
     * @param shareId the shareId of the requested share
     * @return the quantity of shares in portfolio
     */
    public long getPortfolioQuantityByShareId(long shareId, long userId) {
        return getAllPortfolioShares(userId).stream()
                .filter(x -> x.getShareId() == shareId)
                .filter(x -> x.getOrderType() == PortfolioShare.shareState.BOUGHT)
                .map(PortfolioShare::getQuantity)
                .reduce(0L, (x, y) -> x = x + y) -
                getAllPortfolioShares(userId).stream()
                        .filter(x -> x.getShareId() == shareId)
                        .filter(x -> x.getOrderType() == PortfolioShare.shareState.SOLD)
                        .map(PortfolioShare::getQuantity)
                        .reduce(0L, (x, y) -> x = x + y);
    }

    /**
     * Calculates the cumulated price of a share in the portfolio at which it was bought in average
     * considering all transactions.
     *
     * @param shareId the shareId of the requested share
     * @return the calculated price
     */
    public double calculateCumulatedBuyingPriceById(long shareId, long userId) {
        return getAllPortfolioShares(userId).stream()
                .filter(x -> x.getShareId() == shareId)
                .filter(x -> x.getOrderType() == PortfolioShare.shareState.BOUGHT)
                .map(PortfolioShare::getPrice)
                .reduce(0.0, (x, y) -> x = x + y) -
                getAllPortfolioShares(userId).stream()
                        .filter(x -> x.getShareId() == shareId)
                        .filter(x -> x.getOrderType() == PortfolioShare.shareState.SOLD)
                        .map(PortfolioShare::getPrice)
                        .reduce(0.0, (x, y) -> x = x + y) / getPortfolioQuantityByShareId(shareId, userId);
    }

    /**
     * Calculates the difference between the cumulated buying price and the current price of a share in percent.
     *
     * @param shareId the shareId of the requested share
     * @return a positive or negative calculated percentage
     */
    public double getPortfolioChangeInPercentByShareId(long shareId, long userId) {
        double newestPrice = shareInfoRepository.findById(shareId).get().getCurrentPrice();
        double calculatedPrice = calculateCumulatedBuyingPriceById(shareId, userId);
        if (calculatedPrice >= newestPrice) {
            return (calculatedPrice / newestPrice - 1) * 100;
        } else {
            return (1 - calculatedPrice / newestPrice) * -100;
        }
    }


    @Transactional
    public PortfolioShare buyShares(PortfolioShareDTO share) {
        validateBuySell(share);

        if (share.quantity() * share.price() > userRepository.findById(share.userId()).get().getCash()) {
            throw new InvalidTransactionException("Not enough cash available");
        }

        if (!share.orderType().equals("BOUGHT")) {
            throw new InvalidTransactionException("Buy state of Transaction was incorrect");
        }

        var shareToAdd = new PortfolioShare(
                share.shareId(),
                share.userId(),
                share.date_time(),
                share.price(),
                share.quantity(),
                share.orderType()
        );

        userRepository.findById(share.userId()).ifPresent(x -> x.setCash(x.getCash() - share.price() * share.quantity()));
        return portfolioShareRepository.save(shareToAdd);
    }

    @Transactional
    public PortfolioShare sellShares(PortfolioShareDTO share) {
        validateBuySell(share);

        long availableShares = getPortfolioQuantityByShareId(share.shareId(), share.userId());
        if (share.quantity() > availableShares) {
            throw new InvalidTransactionException("Not enough cash available");
        }

        if (!share.orderType().equals("SOLD")) {
            throw new InvalidTransactionException("Sell state of Transaction was incorrect");
        }

        var shareToAdd = new PortfolioShare(
                share.shareId(),
                share.userId(),
                share.date_time(),
                share.price(),
                share.quantity(),
                share.orderType()
        );

        userRepository.findById(share.userId()).ifPresent(x -> x.setCash(x.getCash() + share.price() * share.quantity()));
        return portfolioShareRepository.save(shareToAdd);
    }

    private void validateBuySell(PortfolioShareDTO share) {
        if (userRepository.findById(share.userId()).isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        if (shareInfoRepository.findById(share.shareId()).isEmpty()) {
            throw new ShareNotFoundException("Share not found");
        }
        if (share.quantity() <= 0) {
            throw new InvalidTransactionException("Transaction quantity zero or negative");
        }
    }

    public List<PortfolioShare> getAllPortfolioShares(long userId) {
        var portfolioShares = portfolioShareRepository.findPortfolioSharesByUserId(userId).get();
        return StreamSupport.stream(portfolioShares.spliterator(), true).toList();
    }

    private List<PortfolioValue> getDataPointsPortfolioDay(long userId) {
        List<PortfolioValue> portfolioValues;
        int dayMinus = 0;
        do {
            portfolioValues = portfolioValueRepository.findPortfolioValueByUserIdAndDateTimeAfter(userId, convertLDTToString(getLDTTodayMorning().minusDays(dayMinus++))).orElseGet(ArrayList::new);
        } while (portfolioValues.size() == 0 && dayMinus < 10);
        return portfolioValues;
    }

    private List<PortfolioValue> getDataPointsPortfolioWeek(long userId) {
        Optional<List<PortfolioValue>> portfolioValues = portfolioValueRepository.findPortfolioValueByUserIdAndDateTimeAfter(userId, convertLDTToString(getLDTWeekAgo()));
        return portfolioValues.orElseGet(ArrayList::new);
    }

    private List<PortfolioValue> getDataPointsPortfolioMonth(long userId) {
        Optional<List<PortfolioValue>> portfolioValues = portfolioValueRepository.findPortfolioValueByUserIdAndDateTimeAfter(userId, convertLDTToString(getLDTMonthAgo()));
        return portfolioValues.orElseGet(ArrayList::new);
    }

    private List<PortfolioValue> getDataPointsPortfolioYear(long userId) {
        Optional<List<PortfolioValue>> portfolioValues = portfolioValueRepository.findPortfolioValueByUserIdAndDateTimeAfter(userId, convertLDTToString(getLDTYearAgo()));
        return portfolioValues.orElseGet(ArrayList::new);
    }

    private List<PortfolioValue> getDataPointsPortfolioMax(long userId) {
        Optional<List<PortfolioValue>> portfolioValues = portfolioValueRepository.findPortfolioValueByUserId(userId);
        return portfolioValues.orElseGet(ArrayList::new);
    }

    private LocalDateTime getLDTTodayMorning() {
        return LocalDateTime.now(ZoneId.of("America/New_York")).minusHours(LocalDateTime.now(ZoneId.of("America/New_York")).getHour());
    }

    private LocalDateTime getLDTLast24Hours() {
        return LocalDateTime.now(ZoneId.of("America/New_York")).minusDays(1);
    }

    private LocalDateTime getLDTWeekAgo() {
        return LocalDateTime.now(ZoneId.of("America/New_York")).minusDays(7);
    }

    private LocalDateTime getLDTMonthAgo() {
        return LocalDateTime.now(ZoneId.of("America/New_York")).minusMonths(1);
    }

    private LocalDateTime getLDTYearAgo() {
        return LocalDateTime.now(ZoneId.of("America/New_York")).minusYears(1);
    }

    private String convertLDTToString(LocalDateTime dateTime) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy.MM.dd hh:mm:ss");
        return dateTime.format(fmt);
    }

    private LocalDateTime convertStringToLDT(String dateTime) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy.MM.dd hh:mm:ss");
        return LocalDateTime.parse(dateTime, fmt);
    }
}
