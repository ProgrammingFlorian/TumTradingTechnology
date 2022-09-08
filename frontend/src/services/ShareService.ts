/**
 * Service to handle REST communication of shares.
 * For now, dummy values are used instead of actual http request.
 */
import {Share} from "../models/Share";
import {DataPointGraph} from "../models/DataPointGraph";
import {Requests} from "../common/requests";
import {TimeSpan} from "../models/TimeSpan";

// TODO: All API Requests are missing, currently returning with dummy values after a while to simulate loading

const getMarketIsOpen = (): Promise<boolean> => {
    return Requests.getRequest<boolean>("marketIsOpen");
};

const getShare = (isin: string): Promise<Share> => {
    return Requests.getRequest<Share>("share/isin/" + isin);
};

const getBundle = (): Promise<Share[]> => {
    return Requests.getRequest<Share[]>("share/bundle");
}


const search = (query: string): Promise<Share[]> => {
    return Requests.getRequest<Share[]>("search/" + query);
};

const getPortfolioShares = (): Promise<Share[]> => {
    return Requests.getRequest<Share[]>("share/portfolioShares");
};

const getTopAndFlopShare = (): Promise<Share[]> => {
    return Requests.getRequest<Share[]>("share/isin/topAndFlop");
};

const buyShares = (share: Share, number: number): Promise<boolean> => {
    return Requests.getRequest<boolean>("share/transaction/buy/" + share.isin + "/" + number);
}

const sellShares = (share: Share, number: number): Promise<boolean> => {
    return Requests.getRequest<boolean>("share/transaction/sell/" + share.isin + "/" + number);
}


const getShareGraph = (isin: string, timeSpan: TimeSpan): Promise<DataPointGraph[]> => {
    return Requests.getRequest<DataPointGraph[]>("share/getShareGraph/" + isin + "/" + getTimeSpanAsString(timeSpan));
}

const getTimeSpanAsString = (timeSpan: TimeSpan): string => {
    switch (timeSpan) {
        case TimeSpan.DAY:
            return "day";
        case TimeSpan.WEEK:
            return "week";
        case TimeSpan.MONTH:
            return "month";
        case TimeSpan.YEAR:
            return "year"
        case TimeSpan.MAX:
            return "max"
    }
}


export const ShareService = {
    getMarketOpen: getMarketIsOpen,
    getShare,
    search,
    getBundle,
    getPortfolioShares,
    getTopAndFlopShare,
    buyShares,
    sellShares,
    getShareGraph,
};