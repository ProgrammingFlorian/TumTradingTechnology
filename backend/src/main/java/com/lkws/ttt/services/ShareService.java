package com.lkws.ttt.services;

import com.lkws.ttt.model.*;
import com.lkws.ttt.repository.DailyPriceRepository;
import com.lkws.ttt.repository.HourlyPriceRepository;
import com.lkws.ttt.repository.IntradayPriceRepository;
import com.lkws.ttt.repository.ShareInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.StreamSupport;


@Service
@RequiredArgsConstructor
public class ShareService {

    private final ShareInfoRepository shareInfoRepository;
    private final DailyPriceRepository dailyPriceRepository;
    private final HourlyPriceRepository hourlyPriceRepository;
    private final IntradayPriceRepository intradayPriceRepository;

    public static boolean marketIsOpen() {
        LocalDateTime LDT = LocalDateTime.now(ZoneId.of("America/New_York"));
        if (LDT.getDayOfWeek() == DayOfWeek.SATURDAY || LDT.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return false;
        } else {
            return LDT.getHour() >= 9 && (LDT.getHour() != 9 || LDT.getMinute() >= 29) && LDT.getHour() < 16;
        }
    }

    public Optional<ShareInfo> getShare(String isin) {
        return shareInfoRepository.findShareInfoByIsin(isin);
    }

    public ShareInfo[] getBundle() {
        var sharesAsList = shareInfoRepository.findAll();
        return StreamSupport.stream(sharesAsList.spliterator(), true).toList().toArray(new ShareInfo[0]);
    }

    public ShareInfo getFlopShare() {
       return shareInfoRepository.findFlopShare().orElseThrow(() -> new ShareNotFoundException("Flop share not found"));
    }
    public ShareInfo getTopShare() {
        return shareInfoRepository.findTopShare().orElseThrow(() -> new ShareNotFoundException("Top share not found"));
    }

    private double getFirstIntradayPrice(ShareInfo shareInfo) {
        List<IntradayPrice> prices;
        int dayMinus = 1;
        do {
            prices = intradayPriceRepository.findIntradayPriceByShareIdAndDateAfter(shareInfo.getId(), convertLDTToString(getLDTTodayMorning().minusDays(dayMinus++))).orElseGet(ArrayList::new);
        } while (prices.size() == 0 && dayMinus < 10);
        return prices.get(prices.size() - 1).getPrice();
    }

    public ShareInfo[] search(String query) {
        var allSharesIterable = shareInfoRepository.findAll();
        List<ShareInfo> allShares = new ArrayList<>();
        allSharesIterable.forEach(allShares::add);
        List<ShareInfo> result = new ArrayList<>();
        for (ShareInfo shareInfo : allShares) {
            if (matchesQuery(shareInfo, query)) {
                result.add(shareInfo);
            }
        }
        ShareInfo[] toReturn = new ShareInfo[result.size()];
        return result.toArray(toReturn);
    }

    private boolean matchesQuery(ShareInfo share, String query) {
        if (share == null || query == null) { return false; }
        String queryLowercase = query.toLowerCase(Locale.ROOT);
        String isin = share.getIsin();
        String symbol = share.getSymbol();
        String companyName = share.getCompanyName();
        String exchange = share.getExchange();
        String sector = share.getSector();
        String website = share.getWebsite();
        return  isin != null && isin.toLowerCase(Locale.ROOT).contains(queryLowercase) ||
                symbol != null && symbol.toLowerCase(Locale.ROOT).contains(queryLowercase) ||
                companyName != null && companyName.toLowerCase(Locale.ROOT).contains(queryLowercase) ||
                exchange != null && exchange.toLowerCase(Locale.ROOT).contains(queryLowercase) ||
                sector != null && sector.toLowerCase(Locale.ROOT).contains(queryLowercase) ||
                website != null && website.toLowerCase(Locale.ROOT).contains(queryLowercase);
    }

    public List<IntradayPrice> getDataPointsGraphDay(String isin) {
        List<IntradayPrice> prices;
        int dayMinus = 0;
        do {
            prices = intradayPriceRepository.findIntradayPriceByShareIdAndDateAfter(getShareByISIN(isin).getId(), convertLDTToString(getLDTTodayMorning().minusDays(dayMinus++))).orElseGet(ArrayList::new);
        } while (prices.size() == 0 && dayMinus < 10);
        return prices;
    }

    public List<HourlyPrice> getDataPointsGraphWeek(String isin) {
        Optional<List<HourlyPrice>> hourlyPrices = hourlyPriceRepository.findHourlyPriceByShareIdAndDateTimeAfter(getShareByISIN(isin).getId(), convertLDTToString(getLDTWeekAgo()));
        return hourlyPrices.orElseGet(ArrayList::new);
    }

    public List<HourlyPrice> getDataPointsGraphMonth(String isin) {
        Optional<List<HourlyPrice>> hourlyPrices = hourlyPriceRepository.findHourlyPriceByShareIdAndDateTimeAfter(getShareByISIN(isin).getId(), convertLDTToString(getLDTMonthAgo()));
        return hourlyPrices.orElseGet(ArrayList::new);
    }

    public List<DailyPrice> getDataPointsGraphYear(String isin) {
        Optional<List<DailyPrice>> dailyPrices = dailyPriceRepository.findDailyPriceByShareIdAndDateAfter(getShareByISIN(isin).getId(), convertLDTToString(getLDTYearAgo()));
        return dailyPrices.orElseGet(ArrayList::new);
    }

    public List<DailyPrice> getDataPointsGraphMax(String isin) {
        Optional<List<DailyPrice>> dailyPrices = dailyPriceRepository.findDailyPriceByShareId(getShareByISIN(isin).getId());
        dummyList.add(new DailyPrice(getFlopShare().getId(), LocalDateTime.now(), 100));
        return dailyPrices.orElseGet(ArrayList::new);
    }

    public List<IntradayPrice> getNewestPricesList(String isin) {
        Optional<List<IntradayPrice>> dailyPrices = intradayPriceRepository.findIntradayPriceByShareId(getShareByISIN(isin).getId());
        return dailyPrices.orElseGet(ArrayList::new);
    }

    private ArrayList<DailyPrice> dummyList = new ArrayList<>();

    private ShareInfo getShareByISIN(String isin) {
        return shareInfoRepository.findShareInfoByIsin(isin).orElseThrow(() -> new ShareNotFoundException("Share not found with ISIN: " + isin));
    }

    private LocalDateTime getLDTYearAgo() {
        return LocalDateTime.now(ZoneId.of("America/New_York")).minusYears(1);
    }

    private LocalDateTime getLDTWeekAgo() {
        return LocalDateTime.now(ZoneId.of("America/New_York")).minusDays(7);
    }

    private LocalDateTime getLDTMonthAgo() {
        return LocalDateTime.now(ZoneId.of("America/New_York")).minusMonths(1);
    }

    private LocalDateTime getLDTTodayMorning() {
        return LocalDateTime.now(ZoneId.of("America/New_York")).minusHours(LocalDateTime.now(ZoneId.of("America/New_York")).getHour());
    }

    public double calculatePercentage(ShareInfo share) {
        double firstPrice = getFirstIntradayPrice(share);
        return share.getCurrentPrice() / firstPrice * 100;
    }

    private boolean isTenMinuteTime(String date) {
        LocalDateTime localDateTime = convertStringToLDT(date);
        return localDateTime.getMinute() % 10 == 0;
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
