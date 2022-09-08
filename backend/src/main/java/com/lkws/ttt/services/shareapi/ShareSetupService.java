package com.lkws.ttt.services.shareapi;

import com.lkws.ttt.model.*;
import com.lkws.ttt.repository.*;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLOutput;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class ShareSetupService {


    /**
     * SWITCH FÜR DATENBANK SETUP über Config-File
     *
     * Modi:
     * "DEMO": das Demoportfolio wird initialisiert → modifiziertes Setup und Realtime (Mischung aus full und fill) NOCH IN DER ENTWICKLUNG
     * "FULL": vorhandene Daten werden gelöscht → volles Setup und Realtime
     * "FILL": vorhandene Daten werden ergänzt → reduziertes Setup und Realtime
     * "NOTHING": nur vorhandene Daten werden genutzt → kein Setup und kein Realtime
     * "RESET": wie FULL nur mit Löschen der Portfolio Values und Portfolio Stocks
     */
    private SetupMode SETUP_MODE = SetupMode.FILL;

    /**
     * SWITCH FÜR STOCK BUNDLE (NICHT relevant bei Demo-Modus)
     *
     * Modi:
     * "test": → 3 Stocks
     * "dowjones": → 30 Stocks
     * "r70": → 70 Stocks
     * "s&p500": → 500 Stocks
     */
    private String SYMBOL_BUNDLE = "dowjones";
    private String API_KEY = "todo";
    private String QUERY_BASE = "https://financialmodelingprep.com/api/v3/";
    private String FROM_DATE = "2021-06-01";
    private String TO_DATE = "2022-07-22";

    private final ShareInfoRepository shareInfoRepository;
    private final DailyPriceRepository dailyPriceRepository;
    private final HourlyPriceRepository hourlyPriceRepository;
    private final IntradayPriceRepository intradayPriceRepository;

    private final PortfolioValueRepository portfolioValueRepository;
    private final PortfolioShareRepository portfolioShareRepository;
    private final UserRepository userRepository;


    /**
     * Method that setups the database when starting the spring application
     */
    @PostConstruct
    public void setup() {
        long startTime = System.nanoTime();
        switch (SETUP_MODE) {
            case DEMO -> demoSetup();
            case FULL -> fullSetup();
            case FILL -> fillSetup();
            case RESET -> resetSetup();
        }
        long endTime = System.nanoTime();
        System.out.println("Setup-Time " + (endTime - startTime) +" nano sec");
    }

    /**
     *  Methods for setup
     *  - demo
     *  - full
     *  - fill
     *  - reset
     */

    private void demoSetup() {
        System.out.println("Demo Setup started");

        Optional<User> user = userRepository.findByUsername("demouser@ttt.de");
        if (user.isEmpty()) {
            User demoUser = new User("demouser@ttt.de", "$2a$10$jGq0EKda66wf2gTAlACp0OSXsmy6n5VCf1MW4S5kNQjPgNKzVdaKq", 47281.125);
            userRepository.save(demoUser);
            Long demoUserId = userRepository.findByUsername("demouser@ttt.de").get().getId();
            List<PortfolioValue> demoValues = createDemoPortfolioValues(demoUserId);
            portfolioValueRepository.saveAll(demoValues);

            ShareInfo appleShare = shareInfoRepository.findBySymbol("AAPL").get();
            ShareInfo jpmShare = shareInfoRepository.findBySymbol("JPM").get();
            PortfolioShare appleBought = new PortfolioShare(appleShare.getId(), demoUserId, convertStringToLDT("2022-07-11 12:00:00"),
                    145.485, 15, "BOUGHT");
            PortfolioShare jpmBought = new PortfolioShare(jpmShare.getId(), demoUserId, convertStringToLDT("2022-07-14 13:00:00"),
                    107.32, 5, "BOUGHT");
            portfolioShareRepository.save(appleBought);
            portfolioShareRepository.save(jpmBought);
            System.out.println("Demo erstellt");
        }
        System.out.println("Demo Setup finished");
    }

    private void fullSetup() {
        System.out.println("Full Setup started");
        if (clearDatabase()) {
            loadShareInfo(getSupportedSymbols(SYMBOL_BUNDLE));
            loadDailyPrices(FROM_DATE, TO_DATE);
            loadHourlyPrices(FROM_DATE, TO_DATE);
            loadIntraDayPrices();
        } else {
            System.out.println("Something went wrong while setting up");
        }
        System.out.println("Full Setup finished");
    }

    private void fillSetup() {
        if (checkAllShareInfosExisting()) {
            fillUpDailyPrices();
            fillUpHourlyPrices();
            intradayPriceRepository.deleteAll();
            loadIntraDayPrices();
        }
        else {
            fullSetup();
        }
    }

    @Transactional
    public void resetSetup() {
        System.out.println("Reset Setup started");
        portfolioShareRepository.deleteAll();
        portfolioValueRepository.deleteAll();
        fullSetup();
        System.out.println("Reset Setup finished");
    }

    @Transactional
    public boolean clearDatabase() {
        shareInfoRepository.deleteAll();
        dailyPriceRepository.deleteAll();
        hourlyPriceRepository.deleteAll();
        intradayPriceRepository.deleteAll();

        return shareInfoRepository.count() == 0 && intradayPriceRepository.count() == 0 && hourlyPriceRepository.count() == 0 && dailyPriceRepository.count() == 0;
    }

    /**
     *  Methods for creating objects out of the received data
     *  - shareInformation
     *  - daily
     *  - hourly
     *  - intraday
     */

    private void loadShareInfo(String[] symbols) {
        for (String share : symbols) {
            try {
                if (!shareInfoRepository.findBySymbol(share).isPresent()) {

                    JSONObject jsonObject = shareInformationData(share);
                    JSONObject changeObject = liveChangeData(share).getJSONObject(0);

                    ShareInfo shareInfo = new ShareInfo(
                            jsonObject.getString("isin"),
                            jsonObject.getString("symbol"),
                            jsonObject.getDouble("price"),
                            changeObject.getDouble("1D"),
                            jsonObject.getString("companyName"),
                            jsonObject.getString("currency"),
                            jsonObject.getString("exchange"),
                            jsonObject.getDouble("lastDiv"),
                            jsonObject.getLong("mktCap"),
                            jsonObject.getString("sector"),
                            jsonObject.getString("website"));

                    shareInfoRepository.save(shareInfo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadDailyPrices(String from, String to) {
        List<ShareInfo> shareInfos = getShareInfos();

        for (ShareInfo shareInfo : shareInfos) {
            try {
                long shareId = shareInfo.getId();

                JSONArray response = dailyPriceData(shareInfo.getSymbol(), from, to);

                List<DailyPrice> dailyPriceList = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    JSONObject priceObj = response.getJSONObject(i);
                    dailyPriceList.add(new DailyPrice(
                            shareId,
                            extractDate(priceObj.getString("date")),
                            priceObj.getDouble("close")));
                }

                dailyPriceRepository.saveAll(dailyPriceList);
            } catch (Exception e) {
            }
        }
    }

    private void loadHourlyPrices(String from, String to) {
        List<ShareInfo> shareInfos = getShareInfos();

        for (ShareInfo shareInfo : shareInfos) {
            try {
                long shareId = shareInfo.getId();

                JSONArray response = hourlyPriceData(shareInfo.getSymbol(), from, to);

                List<HourlyPrice> hourlyPriceList = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    JSONObject priceObj = response.getJSONObject(i);
                    hourlyPriceList.add(new HourlyPrice(
                            shareId,
                            extractDate(priceObj.getString("date")),
                            priceObj.getDouble("close"))
                    );

                }
                hourlyPriceRepository.saveAll(hourlyPriceList);
            } catch (Exception e) {
            }
        }
    }

    private void loadIntraDayPrices() {
        List<ShareInfo> shareInfos = getShareInfos();

        for (ShareInfo shareInfo : shareInfos) {
            try {
                long shareId = shareInfo.getId();

                JSONArray response = intraDayPriceData(shareInfo.getSymbol());

                List<IntradayPrice> priceEntryList = new ArrayList<>();

                int i = 0;
                JSONObject priceObj = response.getJSONObject(0);
                String lastTradingDay = priceObj.getString("date").substring(0, 10);
                while (lastTradingDay.equals(priceObj.getString("date").substring(0, 10))) {

                    priceEntryList.add(new IntradayPrice(
                            shareId,
                            extractDate(priceObj.getString("date")),
                            priceObj.getDouble("close")));

                    priceObj = response.getJSONObject(++i);
                }

                intradayPriceRepository.saveAll(priceEntryList);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Transactional
    public void fillUpDailyPrices() {
        String fillUpDate = dailyPriceRepository.findFillUpDate().get().replace('.', '-').substring(0, 10);
        LocalDate date = LocalDate.parse(fillUpDate);
        String nowDate = LocalDate.now(ZoneId.of("America/New_York")).toString().replace('.', '-');

        if (fillUpDate.compareTo(nowDate) < 0) {
            String dateAfter = date.plusDays(1).toString().replace('.', '-');
            loadDailyPrices(dateAfter, nowDate);
        }
        else {
            System.out.println("DailyPrices are already up to date");
        }

    }

    @Transactional
    public void fillUpHourlyPrices() {
        String fillUpDate = hourlyPriceRepository.findFillUpDate().get().replace('.', '-').substring(0, 10);
        LocalDate date = LocalDate.parse(fillUpDate);
        String nowDate = LocalDate.now(ZoneId.of("America/New_York")).toString().replace('.', '-');

        if (fillUpDate.compareTo(nowDate) < 0) {
            String dateAfter = date.plusDays(1).toString().replace('.', '-');
            loadHourlyPrices(dateAfter, nowDate);
        }
        else {
            System.out.println("HourlyPrices are already up to date");
        }

    }

    private List<ShareInfo> getShareInfos() {
        var shares = shareInfoRepository.findAll();
        List<ShareInfo> shareInfos = new ArrayList<>();
        shares.forEach(shareInfos::add);
        return shareInfos;
    }

    private LocalDateTime extractDate(String date) {
        if (date.length() == 10) {
            int year = Integer.parseInt(date.substring(0, 4));
            int month = Integer.parseInt(date.substring(5, 7));
            int day = Integer.parseInt(date.substring(8, 10));
            return LocalDate.of(year, month, day).atTime(16, 0);
        } else if (date.length() > 10) {
            int year = Integer.parseInt(date.substring(0, 4));
            int month = Integer.parseInt(date.substring(5, 7));
            int day = Integer.parseInt(date.substring(8, 10));
            int hour = Integer.parseInt(date.substring(11, 13));
            int minute = Integer.parseInt(date.substring(14, 16));
            int second = Integer.parseInt(date.substring(17, 19));
            return LocalDateTime.of(year, month, day, hour, minute, second);
        } else {
            throw new IllegalArgumentException("Date doesn't exist");
        }
    }


    /**
     *  Methods for making the API calls
     *  - shareInformation
     *  - daily
     *  - hourly
     *  - intraday
     *  - change
     */

    private JSONObject shareInformationData(String symbol) throws JSONException, IOException {
        URL url = new URL(buildApiQuery("profile", symbol));

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String line; (line = reader.readLine()) != null; ) {
                stringBuilder.append(line);
            }
            return (new JSONArray(stringBuilder.toString())).getJSONObject(0);
        }
    }

    private JSONArray dailyPriceData(String symbol, String from, String to) throws JSONException, IOException {

        URL url = new URL(buildApiQuery("historical-price-full", symbol, "serietype=line&from="+from+"&to="+to));
        System.out.println("API query: " + url);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String line; (line = reader.readLine()) != null; ) {
                stringBuilder.append(line);
            }
            return (new JSONObject(stringBuilder.toString())).getJSONArray("historical");
        }
    }

    private JSONArray hourlyPriceData(String symbol, String from, String to) throws JSONException, IOException {
        URL url = new URL(buildApiQuery("historical-chart/1hour", symbol, "serietype=line&from="+from+"&to="+to));
        System.out.println("API query: " + url);
        return hourlyAndIntraDayCall(url);
    }

    private JSONArray intraDayPriceData(String symbol) throws JSONException, IOException {
        URL url = new URL(buildApiQuery("historical-chart/1min", symbol));
        System.out.println("API query: " + url);
        return hourlyAndIntraDayCall(url);
    }

    private JSONArray hourlyAndIntraDayCall(URL url) throws JSONException, IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String line; (line = reader.readLine()) != null; ) {
                stringBuilder.append(line);
            }
            return new JSONArray(stringBuilder.toString());
        }
    }

    private JSONArray liveChangeData(String symbols) throws JSONException, IOException {
        URL url = new URL(QUERY_BASE + "stock-price-change/" + symbols + "?apikey=" + API_KEY);
        System.out.println("API query: " + url);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String line; (line = reader.readLine()) != null; ) {
                stringBuilder.append(line);
            }
            return new JSONArray(stringBuilder.toString());
        }
    }


    private String buildApiQuery(String datatype, String stock, String modification) {
        return QUERY_BASE + datatype + "/" + stock + "?" + modification + "&apikey=" + API_KEY;
    }

    private String buildApiQuery(String datatype, String stock) {
        return QUERY_BASE + datatype + "/" + stock + "?apikey=" + API_KEY;
    }

    private boolean checkAllShareInfosExisting() {
        List<ShareInfo> shareInfos = getShareInfos();
        if (hourlyPriceRepository.countAllShareIds().get() == shareInfos.size() && dailyPriceRepository.countAllShareIds().get() == shareInfos.size())
        {
            List<Long> idsInHourly = hourlyPriceRepository.findAllShareIds().get();
            List<Long> idsInDaily = dailyPriceRepository.findAllShareIds().get();

            boolean inHourly = false;
            boolean inDaily = false;

            for(ShareInfo share : shareInfos) {
                Long shareId = share.getId();
                inDaily = false;
                inHourly = false;
                for (int i = 0; i < idsInHourly.size(); i++) {
                    if (idsInHourly.get(i).equals(shareId)) {
                        inHourly = true;
                    }
                    if (idsInDaily.get(i).equals(shareId)) {
                        inDaily = true;
                    }
                }
                if (!inHourly || !inDaily) {
                    return false;
                }
            }
        }
        return true;
    }

    private static String[] getSupportedSymbols(String bundle) {
        String fileName = switch (bundle) {
            case "dowjones" -> "DowJones.txt";
            case "r70" -> "R70.txt";
            case "test" -> "TestSymbols.txt";
            case "s&p500" -> "S&P-500.txt";
            default -> "DemoSymbols.txt";
        };

        String pathFromRepositoryRoot = "backend/src/main/java/com/lkws/ttt/services/shareapi/sharelists/";
        Path path = Path.of(pathFromRepositoryRoot + fileName);
        List<String> lines = new ArrayList<>();
        try {
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines.toArray(String[]::new);
    }

    private void createDemoOldVersion(long userId) {
        String portfolioStartDate = "2022-07-01";
        String buyDate1 = "2022-07-11 12";
        String buyDate2 = "2022-07-14 13";
        String buyQuantity1 = "15";
        String buyQuantity2 = "5";

        Long shareIdApple = shareInfoRepository.findBySymbol("AAPL").get().getId();
        Long shareIdJPM = shareInfoRepository.findBySymbol("JPM").get().getId();

        String dateFormat = hourlyPriceRepository.getFirstDate().get();

        List<HourlyPrice> applePrices = hourlyPriceRepository.findAllDatesFromAndAfterForShare(portfolioStartDate, shareIdApple).get();
        List<HourlyPrice> jpmPrices = hourlyPriceRepository.findAllDatesFromAndAfterForShare(portfolioStartDate, shareIdJPM).get();
        for (HourlyPrice price : jpmPrices) {
            //System.out.println(price.getDateTime() + " " + price.getPrice());
        }

        List<PortfolioValue> portfolioValues = new ArrayList<>();
        List<PortfolioValue> portfolioValuesTmp = new ArrayList<>();
        List<PortfolioValue> portfolioValuesTmp2 = new ArrayList<>();
        List<PortfolioValue> portfolioValuesTmp3 = new ArrayList<>();
        double remainingCashAfterApple = 47817.725;
        for (HourlyPrice price : applePrices) {
            if(price.getDateTime().substring(0, 13).compareTo(buyDate1.substring(0,13)) >= 0) {

                double stockValues = 15 * price.getPrice();
                double portfolioValue = stockValues + remainingCashAfterApple;

                if (price.getDateTime().substring(0, 13).compareTo(buyDate2.substring(0,13)) < 0) {
                    portfolioValuesTmp3.add(0, new PortfolioValue(-2, price.getDateTime(), portfolioValue));
                }
                else {
                    portfolioValuesTmp.add(0, new PortfolioValue(-2, price.getDateTime(), portfolioValue));
                }
            }
            else {
                portfolioValues.add(0, new PortfolioValue(-2, price.getDateTime(), 50000.));
            }
        }
        for (HourlyPrice price : jpmPrices) {
            if(price.getDateTime().substring(0, 13).compareTo(buyDate2.substring(0,13)) >= 0) {
                for (PortfolioValue value : portfolioValuesTmp) {
                    if (price.getDateTime().equals(value.getDateTime())) {
                        double stockValues = 5 * price.getPrice();
                        double portfolioValue = stockValues + value.getPortfolioValue() - 536.6;
                        portfolioValuesTmp2.add( 0,new PortfolioValue(-2, price.getDateTime(), portfolioValue));
                    }
                }
            }
        }
        for (PortfolioValue value : portfolioValues) {
            System.out.println("\""+value.getDateTime() +"\",");
        }
        for (PortfolioValue value : portfolioValuesTmp3) {
            System.out.println("\""+value.getDateTime() +"\",");
        }
        for (PortfolioValue value : portfolioValuesTmp2) {
            System.out.println("\""+value.getDateTime() +"\",");
        }
        System.out.println("--------");
        for (PortfolioValue value : portfolioValues) {
            System.out.println(value.getPortfolioValue() +",");
        }
        for (PortfolioValue value : portfolioValuesTmp3) {
            System.out.println(value.getPortfolioValue() +", ");
        }
        for (PortfolioValue value : portfolioValuesTmp2) {
            System.out.println(value.getPortfolioValue() +", ");
        }
    }

    private List<PortfolioValue> createDemoPortfolioValues(long userId) {
        double[] values = new double[] {
                50000.0,
                50000.0,
                50000.0,
                50000.0,
                50000.0,
                50000.0,
                50000.0,
                50000.0,
                50000.0,
                50000.0,
                50000.0,
                50000.0,
                50000.0,
                50000.0,
                50000.0,
                50000.0,
                50000.0,
                50000.0,
                50000.0,
                50000.0,
                50000.0,
                50000.0,
                50000.0,
                50000.0,
                50000.0,
                50000.0,
                50000.0,
                50000.0,
                50000.0,
                50000.0,
                50000.0,
                50000.0,
                50000.0,
                50000.0,
                50000.0,
                50000.0,
                50000.0,
                50000.0,
                50013.95,
                49997.525,
                49991.299999999996,
                49992.725,
                50022.5,
                50034.7265,
                50038.373,
                50039.975,
                50019.275,
                50005.625,
                50005.475,
                50002.174999999996,
                49996.025,
                50007.65,
                50009.84,
                50013.576499999996,
                49999.475,
                49995.2765,
                49990.055,
                50024.9885,
                50017.1345,
                50030.9,
                50050.765999999996,
                50048.325,
                50049.675,
                50076.175,
                50072.465,
                50075.975,
                50083.65,
                50088.575,
                50098.275,
                50095.675,
                50114.5,
                50116.039,
                50103.175,
                50057.65,
                50050.175,
                50046.924999999996,
                50043.924999999996,
                50091.525,
                50098.625,
                50103.35,
                50116.802,
                50113.884,
                50118.325000000004,
                50129.725000000006,
                50155.325,
                50150.6,
                50148.525,
                50132.5415,
                50151.536,
                50149.325,
                50139.774999999994,
                50151.075000000004,
                50164.575000000004,
                50185.125,
                50181.525,
                50182.961,
                50188.475,
                50184.924999999996
        };
        String[] dates = new String[] {
                "2022-07-01 10:00:00",
                "2022-07-01 11:00:00",
                "2022-07-01 12:00:00",
                "2022-07-01 13:00:00",
                "2022-07-01 14:00:00",
                "2022-07-01 15:00:00",
                "2022-07-01 16:00:00",
                "2022-07-05 10:00:00",
                "2022-07-05 11:00:00",
                "2022-07-05 12:00:00",
                "2022-07-05 13:00:00",
                "2022-07-05 14:00:00",
                "2022-07-05 15:00:00",
                "2022-07-05 16:00:00",
                "2022-07-06 10:00:00",
                "2022-07-06 11:00:00",
                "2022-07-06 12:00:00",
                "2022-07-06 13:00:00",
                "2022-07-06 14:00:00",
                "2022-07-06 15:00:00",
                "2022-07-06 16:00:00",
                "2022-07-07 10:00:00",
                "2022-07-07 11:00:00",
                "2022-07-07 12:00:00",
                "2022-07-07 13:00:00",
                "2022-07-07 14:00:00",
                "2022-07-07 15:00:00",
                "2022-07-07 16:00:00",
                "2022-07-08 10:00:00",
                "2022-07-08 11:00:00",
                "2022-07-08 12:00:00",
                "2022-07-08 13:00:00",
                "2022-07-08 14:00:00",
                "2022-07-08 15:00:00",
                "2022-07-08 16:00:00",
                "2022-07-11 10:00:00",
                "2022-07-11 11:00:00",
                "2022-07-11 12:00:00",
                "2022-07-11 13:00:00",
                "2022-07-11 14:00:00",
                "2022-07-11 15:00:00",
                "2022-07-11 16:00:00",
                "2022-07-12 10:00:00",
                "2022-07-12 11:00:00",
                "2022-07-12 12:00:00",
                "2022-07-12 13:00:00",
                "2022-07-12 14:00:00",
                "2022-07-12 15:00:00",
                "2022-07-12 16:00:00",
                "2022-07-13 10:00:00",
                "2022-07-13 11:00:00",
                "2022-07-13 12:00:00",
                "2022-07-13 13:00:00",
                "2022-07-13 14:00:00",
                "2022-07-13 15:00:00",
                "2022-07-13 16:00:00",
                "2022-07-14 10:00:00",
                "2022-07-14 11:00:00",
                "2022-07-14 12:00:00",
                "2022-07-14 13:00:00",
                "2022-07-14 14:00:00",
                "2022-07-14 15:00:00",
                "2022-07-14 16:00:00",
                "2022-07-15 10:00:00",
                "2022-07-15 11:00:00",
                "2022-07-15 12:00:00",
                "2022-07-15 13:00:00",
                "2022-07-15 14:00:00",
                "2022-07-15 15:00:00",
                "2022-07-15 16:00:00",
                "2022-07-18 10:00:00",
                "2022-07-18 11:00:00",
                "2022-07-18 12:00:00",
                "2022-07-18 13:00:00",
                "2022-07-18 14:00:00",
                "2022-07-18 15:00:00",
                "2022-07-18 16:00:00",
                "2022-07-19 10:00:00",
                "2022-07-19 11:00:00",
                "2022-07-19 12:00:00",
                "2022-07-19 13:00:00",
                "2022-07-19 14:00:00",
                "2022-07-19 15:00:00",
                "2022-07-19 16:00:00",
                "2022-07-20 10:00:00",
                "2022-07-20 11:00:00",
                "2022-07-20 12:00:00",
                "2022-07-20 13:00:00",
                "2022-07-20 14:00:00",
                "2022-07-20 15:00:00",
                "2022-07-20 16:00:00",
                "2022-07-21 10:00:00",
                "2022-07-21 11:00:00",
                "2022-07-21 12:00:00",
                "2022-07-21 13:00:00",
                "2022-07-21 14:00:00",
                "2022-07-21 15:00:00",
                "2022-07-21 16:00:00"
        };
        List<PortfolioValue> portfolioValues = new ArrayList<>();
        for (int i = 0; i < values.length && i < dates.length; i++) {
            portfolioValues.add(new PortfolioValue(userId, convertStringToLDT(dates[i]), values[i]));
        }
        return portfolioValues;
    }

    private LocalDateTime convertStringToLDT(String dateTime)
    {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(dateTime, fmt);
    }

    private enum SetupMode {
        RESET, DEMO, FULL, FILL, NOTHING
    }

}
