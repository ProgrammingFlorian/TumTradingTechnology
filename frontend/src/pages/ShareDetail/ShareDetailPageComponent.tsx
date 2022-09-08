import {emptyShare, Share} from "../../models/Share";
import ShareDetailComponent from "./components/ShareDetailWindowComponent";
import BuySellWindowComponent from "./components/BuySellWindowComponent";
import ShareDetailState from "../../states/ShareDetailState";
import {useEffect, useState} from "react";
import InBuyingModeState from "../../states/InBuyingModeState";
import {DataPointGraph} from "../../models/DataPointGraph";
import {TimeSpan} from "../../models/TimeSpan";
import PerformanceChartComponent from "../../components/PerformanceChart/PerformanceChartComponent";
import TransactionState from "../../states/TransactionState";

// CSS Files
import "./ShareDetailPageStyle.css";
import {Formats} from "../../common/Formats";

interface ShareDetailPageProps {
    share: Share;
    buyShares: (number: number) => void;
    sellShares: (number: number) => void;
    isTransactionSuccess: TransactionState;
    cash: number;
    resetTransactionSuccess: () => void;
    dataPointsGraph: DataPointGraph[];
    onTimeSpanButtonClick: (balance: number, timeSpan: TimeSpan) => void;
    percentageOfShare: number;
    changeInPrice: number;
    updateAll: () => void;
}

interface ShareInformationProps {
    share: Share;
    buyShares: (number: number) => void;
    sellShares: (number: number) => void;
    buySellState: ShareDetailState;
    setBuySellState: (state: ShareDetailState) => void;
    isInOverview: boolean;
    setIsInOverview: (isInOverview: boolean) => void;
    isTransactionSuccess: TransactionState;
    cash: number;
    resetTransactionSuccess: () => void;
    updateAll: () => void;
}

const ShareDetailPageComponent = (props: ShareDetailPageProps) => {
    const [buySellState, setBuySellState] = useState<ShareDetailState>(new InBuyingModeState());
    const [isInOverview, setIsInOverview] = useState<boolean>(true);


    /* TODO @Marius which font style is being used? See shareDetailPageComponent in className and in styleSheet;
        maybe it's not needed anymore
     */
    return (
        <div className="container-fluid py-3" style={{maxWidth: 1300}}>
            <div className="container-fluid g-3 row">
                <div className="col-12">
                    <ShareNameWKNISINPriceComponent share={props.share} percentageOfShare={props.percentageOfShare}
                                                    changeInPrice={props.changeInPrice}/>
                </div>
                <div className="container-fluid col-lg-7 col-12" style={{fontSize: 20}}>
                    <PerformanceChart dataPointsGraph={props.dataPointsGraph}
                                      onTimeSpanButtonClick={props.onTimeSpanButtonClick}/>
                </div>
                <div className="container-fluid col-lg-5 col-12" style={{fontSize: 20}}>
                    {props.share !== null ?
                        <ShareInformation share={props.share}
                                          buySellState={buySellState} setBuySellState={setBuySellState}
                                          setIsInOverview={setIsInOverview} isInOverview={isInOverview}
                                          buyShares={props.buyShares} isTransactionSuccess={props.isTransactionSuccess}
                                          cash={props.cash} sellShares={props.sellShares}
                                          resetTransactionSuccess={props.resetTransactionSuccess}
                                          updateAll={props.updateAll}/> :
                        <></>
                    }
                </div>
            </div>
        </div>
    );
};

interface PerformanceChartProps {
    dataPointsGraph: DataPointGraph[];
    onTimeSpanButtonClick: (balance: number, timeSpan: TimeSpan) => void;

}

const PerformanceChart = (props: PerformanceChartProps) => {
    return (
        <div className="container-fluid h-100 d-flex align-items-center" style={{
            borderRadius: 8,
            backgroundColor: "#1a1a1a"
        }}>
            <PerformanceChartComponent dataPointsGraph={props.dataPointsGraph}
                                       onTimeSpanButtonClick={props.onTimeSpanButtonClick}/>
        </div>
    );
};

interface ShareNameWKNISINPriceProps {
    share: Share;
    percentageOfShare: number;
    changeInPrice: number;
}

const ShareNameWKNISINPriceComponent = (props: ShareNameWKNISINPriceProps) => {
    // Show the div with empty information and hidden for correct layout (height)
    // (otherwise the div below jumps down when this one is loaded)
    // Therefore, set opacity = 0 and only animate the transition to opacity = 1 when loaded
    // and replaced with correct data
    const shareNameWKNISINPriceComponentClasses = `container-fluid ${(props.share !== null ? 'anim-opacity' : '')}`;
    const share = props.share ?? emptyShare;

    return (
        <div className={shareNameWKNISINPriceComponentClasses} style={{opacity: 0}}>
            <div className="row d-flex">
                <div className="col-12 col-sm-6">
                    <h1 className="text-white ">{share.companyName}</h1>
                    <h6 className="text-white">
                        <div>Ticker:&nbsp;{share.symbol}</div>
                        <div>ISIN:&nbsp;{share.isin}</div>
                    </h6>

                </div>
                <div className=" col-12 text-start col-sm-6 text-sm-end">
                    <h1 className="text-white">{share.currentPrice.toLocaleString('de-DE', Formats.currency)}</h1>
                    <div className="text-white">
                        <p>
                            <span
                                style={{fontSize: 22}}>{handleChangeInValue(props.changeInPrice, " $", false)}</span>&nbsp;&nbsp;
                        </p>
                    </div>
                </div>
            </div>
        </div>
    );
};

//{handleChangeInValue(props.percentageOfShare, "%", true)}

const ShareInformation = (props: ShareInformationProps) => {
    let content;
    if (props.isInOverview) {
        content = <ShareDetailComponent share={props.share} setBuySellState={props.setBuySellState}
                                        setIsInOverview={props.setIsInOverview}
                                        updateAll={props.updateAll}/>;
    } else {
        content = <BuySellWindowComponent share={props.share}
                                          buyShares={props.buyShares}
                                          cash={props.cash} sellShares={props.sellShares}
                                          isTransactionSuccess={props.isTransactionSuccess}
                                          buySellState={props.buySellState} setIsInOverview={props.setIsInOverview}
                                          resetTransactionSuccess={props.resetTransactionSuccess}
                                          updateAll={props.updateAll}/>;
    }

    return (
        <div className="container-fluid h-100 d-flex align-items-center py-3 anim-opacity" style={{
            borderRadius: 8,
            backgroundColor: "#1a1a1a"
        }}>
            {content}
        </div>
    );
};

const handleChangeInValue = (value: number, unit: string, parentheses: boolean) => {
    let classes = 'mb-0 ';
    let sign;
    classes += (value < 0) ? 'text-danger' : 'text-success';
    sign = (value < 0) ? '' : '+';

    return (
        parentheses ?
            <text className={classes}>({sign}{value.toLocaleString("de-DE", {maximumFractionDigits: 2})}{unit})</text> :
            <text className={classes}>{sign}{value.toLocaleString("de-DE", {
                maximumFractionDigits: 2,
                minimumFractionDigits: 2
            })}{unit}</text>
    );
};


export default ShareDetailPageComponent;