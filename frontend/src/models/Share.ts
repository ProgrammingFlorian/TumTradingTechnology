export interface Share {
    isin: string;
    symbol: string;
    currentPrice: number;
    companyName: string;
    percentage: number;
    exchange?: string
    dividend: number;
    marketCap: number;
    sector: string;
    website?: string;
    portfolioQuantity: number;
}

export const emptyShare: Share = {
    isin: "",
    symbol: "",
    currentPrice: 0,
    companyName: "",
    percentage: 0,
    exchange: "",
    dividend: 0,
    marketCap: 0,
    sector: "",
    website: "",
    portfolioQuantity: 0
};