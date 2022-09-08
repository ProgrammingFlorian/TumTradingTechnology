package com.lkws.ttt.controller;

import com.lkws.ttt.datatransferobjects.DataPointGraphDTO;
import com.lkws.ttt.datatransferobjects.PortfolioShareDTO;
import com.lkws.ttt.datatransferobjects.ShareDTO;
import com.lkws.ttt.model.ShareInfo;
import com.lkws.ttt.model.TimeSpan;
import com.lkws.ttt.model.User;
import com.lkws.ttt.model.UserNotFoundException;
import com.lkws.ttt.services.PortfolioService;
import com.lkws.ttt.services.ShareService;
import com.lkws.ttt.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static com.lkws.ttt.model.TimeSpan.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ShareController {

    private final ShareService shareService;
    private final PortfolioService portfolioService;
    private final UserService userService;

    ShareInfo dummyShare = new ShareInfo();

    @GetMapping("/marketIsOpen")
    public ResponseEntity<Boolean> marketIsOpen() {
        return new ResponseEntity<>(shareService.marketIsOpen(), HttpStatus.OK);
    }

    // TODO: Should be able to use enum instead of String
    // TODO: Use logging instead of sout (whole file)
    @GetMapping("/share/isin/{string}")
    public ResponseEntity<ShareDTO> getShare(Authentication authentication, @PathVariable String string) {
        initDummyShare();
        var username = authentication.getName();
        var user = userService.getUser(username).orElseThrow(() -> new UserNotFoundException("User not found"));
        ShareInfo share = shareService.getShare(string).orElse(dummyShare);
        double percentage = shareService.calculatePercentage(share);
        long quantity = portfolioService.getPortfolioQuantityByShareId(share.getId(), user.getId());
        ShareInfo shareInfo = share;
        shareInfo.setCurrentPrice(share.getCurrentPrice());
        shareInfo.setPriceChange(share.getPriceChange());
        return new ResponseEntity<>(ShareDTO.of(shareInfo, quantity), HttpStatus.OK);
    }

    @GetMapping("/share/isin/topAndFlop")
    public ResponseEntity<ShareDTO[]> getTopAndFlop(Authentication authentication) {
        initDummyShare();
        ShareDTO[] response = new ShareDTO[2];
        response[0] = ShareDTO.of(shareService.getTopShare(), 0);
        response[1] = ShareDTO.of(shareService.getFlopShare(), 0);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/search/{query}")
    public ResponseEntity<ShareDTO[]> search(Authentication authentication, @PathVariable String query) {
        var username = authentication.getName();
        var user = userService.getUser(username).orElseThrow(() -> new UserNotFoundException("Logged-in user not found"));
        var results = shareService.search(query);
        ShareDTO[] resultsDTO = extractShareDTOFromBundle(results, user);
        ShareDTO[] response = new ShareDTO[1];
        response[0] = new ShareDTO("", helper(results), 0, "", 0, "", 0, 0, "", "", 0);
        return new ResponseEntity<>(resultsDTO, HttpStatus.OK);
    }

    private String helper(ShareInfo[] input) {
        StringBuilder sB = new StringBuilder();
        for (ShareInfo sI : input) {
            sB.append(sI.getSymbol()+"/");
        }
        return sB.toString();
    }

    @GetMapping("/share/bundle")
    public ResponseEntity<ShareDTO[]> getBundle(Authentication authentication) {
        var username = authentication.getName();
        var user = userService.getUser(username).orElseThrow(() -> new UserNotFoundException("Logged-in user not found"));
        initDummyShare();
        ShareInfo[] bundle = shareService.getBundle();
        Arrays.stream(bundle).forEach(s -> s.setPriceChange(s.getPriceChange()));
        ShareDTO[] resultsDTO = extractShareDTOFromBundle(bundle, user);
        return new ResponseEntity<>(resultsDTO, HttpStatus.OK);
    }

    private ShareDTO[] extractShareDTOFromBundle(ShareInfo[] results, User user) {
        ShareDTO[] resultsDTO = new ShareDTO[results.length];
        for (int i = 0; i < results.length; i++) {
            resultsDTO[i] = ShareDTO.of(results[i], portfolioService.getPortfolioQuantityByShareId(results[i].getId(), user.getId()));
        }
        return resultsDTO;
    }

    @GetMapping("/share/getShareGraph/{isin}/{timeSpan}")
    public ResponseEntity<DataPointGraphDTO[]> getShareGraph(Authentication authentication, @PathVariable String isin, @PathVariable String timeSpan) {
        DataPointGraphDTO[] dataPointGraphDTOS;
        try {
            if (isin.equals("portfolio")) {
                TimeSpan tspan = switch (timeSpan) {
                    case "day" -> DAY;
                    case "week" -> WEEK;
                    case "month" -> MONTH;
                    case "year" -> YEAR;
                    default -> MAX;
                };
                var username = authentication.getName();
                var user = userService.getUser(username).orElseThrow(() -> new UserNotFoundException("Logged-in user not found"));
                dataPointGraphDTOS = portfolioService.getPortfolioValueDataPoints(user.getId(), tspan).stream().map(pv -> DataPointGraphDTO.of(changeDTFormat(pv.getDateTime()), pv.getPortfolioValue())).toList().toArray(new DataPointGraphDTO[0]);
            } else {
                dataPointGraphDTOS = switch (timeSpan) {
                    case "day" ->
                            shareService.getDataPointsGraphDay(isin).stream().map(dpg -> DataPointGraphDTO.of(changeDTFormat(dpg.getDate()), dpg.getPrice())).toList().toArray(new DataPointGraphDTO[0]);
                    case "week" ->
                            shareService.getDataPointsGraphWeek(isin).stream().map(dpg -> DataPointGraphDTO.of(changeDTFormat(dpg.getDateTime()), dpg.getPrice())).toList().toArray(new DataPointGraphDTO[0]);
                    case "month" ->
                            shareService.getDataPointsGraphMonth(isin).stream().map(dpg -> DataPointGraphDTO.of(changeDTFormat(dpg.getDateTime()), dpg.getPrice())).toList().toArray(new DataPointGraphDTO[0]);
                    case "year" ->
                            shareService.getDataPointsGraphYear(isin).stream().map(dpg -> DataPointGraphDTO.of(changeDTFormat(dpg.getDate()), dpg.getPrice())).toList().toArray(new DataPointGraphDTO[0]);
                    case "max" ->
                            shareService.getDataPointsGraphMax(isin).stream().map(dpg -> DataPointGraphDTO.of(changeDTFormat(dpg.getDate()), dpg.getPrice())).toList().toArray(new DataPointGraphDTO[0]);
                    default -> new DataPointGraphDTO[0];
                };
            }
        } catch (Exception ex) {
            System.err.println("ShareGraph>>>>>>>>>>>>>>>>>>" + ex);
            dataPointGraphDTOS = new DataPointGraphDTO[0];
        }
        if (dataPointGraphDTOS.length == 0) {
            System.out.println("Share Graph fehler with " + isin);
        }

        dataPointGraphDTOS = Arrays.stream(dataPointGraphDTOS).distinct().sorted((a, b) -> changeDTFormatBack(b.date()).isAfter(changeDTFormatBack(a.date())) ? 1 : -1).toList().toArray(new DataPointGraphDTO[0]);
        dataPointGraphDTOS[0] = new DataPointGraphDTO(dataPointGraphDTOS[0].date(), dataPointGraphDTOS[0].price());
        return new ResponseEntity<>(dataPointGraphDTOS, HttpStatus.OK);
    }

    @GetMapping("/share/portfolioShares")
    public ResponseEntity<ShareDTO[]> getPortfolioShares(Authentication authentication) {
        initDummyShare();
        var username = authentication.getName();
        var user = userService.getUser(username).orElseThrow(() -> new UserNotFoundException("Logged-in user not found"));
        ShareInfo[] bundle = portfolioService.getSharesInPortfolio(user.getId()).toArray(ShareInfo[]::new);
        ShareDTO[] resultsDTO = new ShareDTO[bundle.length];
        for (int i = 0; i < bundle.length; i++) {
            resultsDTO[i] = ShareDTO.of(bundle[i], portfolioService.getPortfolioQuantityByShareId(bundle[i].getId(), user.getId()));
        }
        return new ResponseEntity<>(resultsDTO, HttpStatus.OK);
    }

    @GetMapping("/share/transaction/{type}/{isin}/{number}")
    public ResponseEntity<Boolean> buySellShare(@PathVariable String type, @PathVariable String isin, @PathVariable Integer number, Authentication authentication) {
        var username = authentication.getName();
        var user = userService.getUser(username).orElseThrow(() -> new UserNotFoundException("Logged-in user not found"));
        if (type.equals("buy")) {
            portfolioService.buyShares(generatePortfolioShareDTO(isin, number, type, user.getId()));
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else if (type.equals("sell")) {
            portfolioService.sellShares(generatePortfolioShareDTO(isin, number, type, user.getId()));
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    private PortfolioShareDTO generatePortfolioShareDTO(String isin, Integer quantity, String orderType, long userId) {
        if (orderType.equals("buy")) {
            orderType = "BOUGHT";
        } else if (orderType.equals("sell")) {
            orderType = "SOLD";
        }
        ShareInfo shareInfo = shareService.getShare(isin).orElseThrow();
        long shareId = shareInfo.getId();
        double price = shareService.getNewestPricesList(isin).get(0).getPrice();
        return new PortfolioShareDTO(shareId, userId, LocalDateTime.now(), price, quantity, orderType);
    }

    private void initDummyShare() {
        dummyShare.setCurrency("dummy");
        dummyShare.setCompanyName("dummy inc");
        dummyShare.setCurrentPrice(0.0);
        dummyShare.setExchange("dummy");
        dummyShare.setIsin("dummy");
        dummyShare.setMarketCap(1);
        dummyShare.setWebsite("www.dummy.de");
        dummyShare.setSector("dummy sector");
        dummyShare.setDividend(-1);
        dummyShare.setSymbol("DUM");
    }

    private String changeDTFormat(String dateTime) {
        String day = dateTime.substring(8, 10);
        String month = dateTime.substring(5, 7);
        String year = dateTime.substring(0, 4);
        String hour = dateTime.substring(11, 13);
        String minute = dateTime.substring(14, 16);
        return day + "." + month + "." + year + "\n" + hour + ":" + minute;
    }

    private LocalDateTime changeDTFormatBack(String dateTime) {
        String[] parts = dateTime.split("\n");
        int day = Integer.parseInt(parts[0].substring(0, 2));
        int month = Integer.parseInt(parts[0].substring(3, 5));
        int year = Integer.parseInt(parts[0].substring(6, 10));
        int hour = Integer.parseInt(parts[1].substring(0, 2));
        int minute = Integer.parseInt(parts[1].substring(3, 5));
        hour = hour < 5 ? hour + 12 : hour;
        return LocalDateTime.of(year, month, day, hour, minute);
    }

    private LocalDateTime convertStringToLDT(String dateTime) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        return LocalDateTime.parse(dateTime, fmt);
    }


}
