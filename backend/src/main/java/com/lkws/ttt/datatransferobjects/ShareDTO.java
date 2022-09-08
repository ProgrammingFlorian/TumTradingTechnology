package com.lkws.ttt.datatransferobjects;

import com.lkws.ttt.model.ShareInfo;

public record ShareDTO(

        String isin,
        String symbol,
        double currentPrice,
        String companyName,
        double percentage,
        String exchange,
        double dividend,
        long marketCap,
        String sector,
        String website,
        long portfolioQuantity

) {
    public static ShareDTO of(ShareInfo share, long quantity) {
        return new ShareDTO(share.getIsin(), share.getSymbol(), share.getCurrentPrice(), share.getCompanyName(), share.getPriceChange(), share.getExchange(), share.getDividend(), share.getMarketCap(), share.getSector(), share.getWebsite(), quantity);
    }
}
