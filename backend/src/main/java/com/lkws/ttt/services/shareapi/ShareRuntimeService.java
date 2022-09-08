package com.lkws.ttt.services.shareapi;

import com.lkws.ttt.model.IntradayPrice;
import com.lkws.ttt.model.ShareInfo;
import com.lkws.ttt.repository.DailyPriceRepository;
import com.lkws.ttt.repository.HourlyPriceRepository;
import com.lkws.ttt.repository.IntradayPriceRepository;
import com.lkws.ttt.repository.ShareInfoRepository;
import com.lkws.ttt.services.ShareService;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@EnableScheduling
@RequiredArgsConstructor
public class ShareRuntimeService {

    /**
     * SWITCH FÜR DATENBANK BEFÜLLUNG über Config-File
     *
     * Modi:
     * "demo": das Demoportfolio wird initialisiert → modifiziertes Setup und Realtime (Mischung aus full und fill)
     * "full_reload": vorhandene Daten werden gelöscht → volles Setup und Realtime
     * "fill_up": vorhandene Daten werden ergänzt → reduziertes Setup je nach Füllstand und Realtime
     * "nothing": nur vorhandene Daten werden genutzt → kein Setup und kein Realtime
     * "reset": wie full nur löschen der Portfolio Values und Shares
     */
    private SetupMode SETUP_MODE = SetupMode.FILL;
    private String API_KEY = "todo";
    private String QUERY_BASE = "https://financialmodelingprep.com/api/v3/";

    private final ShareInfoRepository shareInfoRepository;
    private final IntradayPriceRepository intradayPriceRepository;
    private List<ShareInfo> shareInfos = new ArrayList<>();


    @Scheduled(fixedRate = 60000)
    public void realTimeData() {
        switch (SETUP_MODE) {
            case FULL, FILL, DEMO, RESET -> loadLivePrices();
        }
    }

    public void loadLivePrices() {
        if (ShareService.marketIsOpen()) {
            if (shareInfos.isEmpty()) {
                shareInfos = getShareInfos();
            }
            if (!shareInfos.isEmpty()) {
                try {
                    String symbolsToUpdate = getSymbolsBundle(shareInfos);
                    JSONArray data = liveData(symbolsToUpdate);
                    JSONArray changes = liveChange(symbolsToUpdate);

                    List<IntradayPrice> priceEntryList = new ArrayList<>();
                    for (int i = 0; i < data.length(); i++) {
                        for (int j = 0; j < shareInfos.size(); j++) {
                            if (shareInfos.get(j).getSymbol().equals(data.getJSONObject(i).getString("symbol"))) {
                                long shareId = shareInfos.get(i).getId();
                                JSONObject priceObject = data.getJSONObject(i);
                                priceEntryList.add(new IntradayPrice(
                                        shareId,
                                        LocalDateTime.now(ZoneId.of("America/New_York")),
                                        priceObject.getDouble("price"))
                                );
                                ShareInfo share = shareInfoRepository.findById(shareId).get();
                                share.setCurrentPrice(priceObject.getDouble("price"));
                                shareInfoRepository.save(share);
                            }
                        }
                    }
                    intradayPriceRepository.saveAll(priceEntryList);
                    for (int i = 0; i < changes.length(); i++) {
                        for (int j = 0; j < shareInfos.size(); j++) {
                            if (shareInfos.get(j).getSymbol().equals(changes.getJSONObject(i).getString("symbol"))) {
                                long shareId = shareInfos.get(i).getId();
                                ShareInfo share = shareInfoRepository.findById(shareId).get();
                                JSONObject changeObject = changes.getJSONObject(i);
                                share.setPriceChange(changeObject.getDouble("1D"));
                                shareInfoRepository.save(share);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Scheduled(cron = "* 29 9 * * 1-5", zone = "America/New_York") // Every weekday at 9:29 am
    public void refreshIntradayDatabase() {
        /**  TODO
         * 1. Get last price at every hour and day
         * 2. Update hourlyPrice and dailyPrice tables
         * 3. Deleate entries in intraday table
         */
    }


    /**
     * Concatenates Symbols in form: "AAPL,AMZN,NVDA,QTT" to make API call
     */
    private String getSymbolsBundle(List<ShareInfo> shareInfos) {
        StringBuilder symbolsToUpdate = new StringBuilder();
        for (ShareInfo shareInfo : shareInfos) {
            symbolsToUpdate.append(shareInfo.getSymbol()).append(",");
        }
        return symbolsToUpdate.substring(0, symbolsToUpdate.length() - 1);
    }

    private JSONArray liveData(String symbols) throws JSONException, IOException {
        URL url = new URL(QUERY_BASE + "quote-short/" + symbols + "?apikey=" + API_KEY);
        System.out.println("API query: " + url);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String line; (line = reader.readLine()) != null; ) {
                stringBuilder.append(line);
            }
            return new JSONArray(stringBuilder.toString());
        }
    }

    private JSONArray liveChange(String symbols) throws JSONException, IOException {
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

    private List<ShareInfo> getShareInfos() {
        List<ShareInfo> shareInfos = shareInfoRepository.findAllSharesOrderedBySymbol().get();
        return shareInfos;
    }

    private enum SetupMode {
       RESET ,DEMO, FULL, FILL, NOTHING
    }
}
