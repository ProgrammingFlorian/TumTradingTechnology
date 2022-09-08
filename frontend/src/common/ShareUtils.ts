import {Share} from "../models/Share";

export const calculatePortfolioValueOfShare = (share: Share): number => {
    return share.portfolioQuantity * share.currentPrice
};