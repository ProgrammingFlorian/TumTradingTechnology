import {Share} from "../../../models/Share";
import ShareDetailState from "../../../states/ShareDetailState";
import InBuyingModeState from "../../../states/InBuyingModeState";
import InSellingModeState from "../../../states/InSellingModeState";
import {Formats} from "../../../common/Formats";

interface ShareDetailWindowProps {
    share: Share;
    setBuySellState: (state: ShareDetailState) => void;
    setIsInOverview: (value: boolean) => void;
    updateAll: () => void;
}

const ShareDetailWindowComponent = (props: ShareDetailWindowProps) => {

    const calculatePortfolioValueOfShares = (): string => {
        return "" + (props.share.portfolioQuantity * props.share.currentPrice).toLocaleString("de-DE", Formats.currency);
    };

    const descriptionAndValue = (description: string, value: any) => {
        return (
            <div className="row text-white py-2" style={{fontSize: "medium"}}>
                <div className="col-7 text-start">{description}</div>
                <div className="col-5 text-end">{value}</div>
            </div>);
    }

    const calculateMarketCapitalization = (): string => {
        if (props.share.marketCap / 1_000_000_000 >= 1) {
            return (props.share.marketCap / 1_000_000_000).toLocaleString("de-DE", Formats.percent) + "B $"
        }
        if (props.share.marketCap / 1_000_000 >= 1) {
            return (props.share.marketCap / 1_000_000).toLocaleString("de-DE", Formats.percent) + "M $"
        }
        return "" + (props.share.marketCap / 1_000).toLocaleString("de-DE", Formats.percent) + "T $"
    }

    const listOfInformation = [
        {
            description: "Shares in portfolio: ",
            cValue: props.share.portfolioQuantity,
        },
        {
            description: "Portfolio value of share: ",
            cValue: calculatePortfolioValueOfShares(),
        },
        {
            description: "Sector: ",
            cValue: props.share.sector
        },
        {
            description: "Market capitalization: ",
            cValue: calculateMarketCapitalization()
        },
        {
            description: "Dividend: ",
            cValue: props.share.dividend.toLocaleString("de-DE", Formats.currency)
        },
        {
            description: "Exchange: ",
            cValue: "" + props.share.exchange
        }
    ];

    return (
        <div className="container-fluid">
            {listOfInformation.map((dAndV) => {
                return (
                    <div>
                        {descriptionAndValue(dAndV.description, dAndV.cValue)}
                    </div>
                );
            })}
            <div className="row text-white py-2" style={{fontSize: "medium"}}>
                <div className="col-1 text-start text-start">{"Website: "}</div>
                <a className="col-11 text-end text-primary" href={props.share.website}
                   target="_blank">{props.share.website}</a>
            </div>
            <div className="py-1">
                {SwitchToBuyingModeButton(props.setBuySellState, props.setIsInOverview, props.updateAll)}
            </div>
            <div className="py-1">
                {SwitchToSellingModeButton(props.setBuySellState, props.setIsInOverview, props.updateAll)}
            </div>
        </div>
    );
};

const SwitchToBuyingModeButton = (setBuySellState: (state: ShareDetailState) => void, setIsInOverview: (value: boolean) => void, updateAll: () => void) => {
    const onClick = () => {
        updateAll();
        setBuySellState(new InBuyingModeState());
        setIsInOverview(false);
    }

    return (
        <button className="btn btn-outline-success w-100" onClick={onClick} style={{
            color: "white", borderColor: "green", borderWidth: "medium"
        }}>
            Buy
        </button>
    )
};

const SwitchToSellingModeButton = (setBuySellState: (state: ShareDetailState) => void, setIsInOverview: (value: boolean) => void, updateAll: () => void) => {
    const onClick = () => {
        updateAll();
        setBuySellState(new InSellingModeState());
        setIsInOverview(false);
    }

    return (
        <button className="btn btn-outline-danger w-100" onClick={onClick} style={{
            color: "white", borderColor: "darkred", borderWidth: "medium"
        }}>
            Sell
        </button>
    )
};

export default ShareDetailWindowComponent;