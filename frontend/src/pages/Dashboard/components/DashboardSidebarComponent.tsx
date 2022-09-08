import React from "react";
import {Share} from "../../../models/Share";
import {Formats} from "../../../common/Formats";

interface SidebarProps {
    onShareClick: (share: Share) => void;
    topShare: Share;
    flopShare: Share;
    currentBalance: number;
    remainingCash: number;
    differenceInBalance: number;
}

const DashboardSidebarComponent = (props: SidebarProps) => {
    return (
        <div className="container-fluid bg-transparent g-0 h-100 hover-shadow-soft box-container anim-opacity">
            <div className="row p-0 g-md-3 g-lg-3 g-xl-3 g-xxl-3 h-100">
                <div className="col-sm-12 col-md-6 col-lg-6 col-xl-6 col-xxl-12 h-auto">
                    <PortfolioBalance balance={props.currentBalance} remainingBalance={props.remainingCash}
                                      differenceBalance={props.differenceInBalance}/>
                </div>
                <div className="col-sm-12 col-md-6 col-lg-6 col-xl-6 col-xxl-12 pt-3 pb-0 pt-md-0 pb-md-0">
                    <TopFlopShares topShare={props.topShare} flopShare={props.flopShare}
                                   onShareClick={props.onShareClick}/>
                </div>
            </div>
        </div>
    );
};

export default DashboardSidebarComponent;


interface PortfolioBalanceProps {
    balance: number;
    remainingBalance: number;
    differenceBalance: number;
}

const PortfolioBalance = (props: PortfolioBalanceProps) => {
    const formattedBalance = props.balance.toLocaleString('de-DE', Formats.currency);
    const formattedRemainingBalance = props.remainingBalance.toLocaleString('de-DE', Formats.currency);

    return (
        <div className="container-fluid justify-content-between h-100 g-3 box-container">
            <div className="row p-2">
                <div className="col-12 col-sm-6 col-md-12 gap-xl-0">
                    <h5 className="pt-2 pt-sm-3">Portfolio</h5>
                    <h1 className="pt-0 font-weight-bold">{formattedBalance}</h1>
                    <div>{coloredArrow(props.differenceBalance, false)}</div>
                </div>
                <div className="col-12 col-sm-6 col-md-12 text-start text-sm-end text-md-start">
                    <h5 className="pt-3 pt-sm-3">Cash</h5>
                    <h3 className="pt-0">{formattedRemainingBalance}</h3>
                </div>
            </div>
        </div>
    );
};


interface TopFlopSharesProps {
    onShareClick?: (share: Share) => void;
    topShare: Share;
    flopShare: Share;
}

const TopFlopShares = (props: TopFlopSharesProps) => {
    return (
        <div className="container-fluid h-100 g-0 p-2 box-container">
            <div className="row h-50">
                <div className="col-12 h-100">
                    <ShareViewSidebarComponent share={props.topShare} onShareClick={props.onShareClick}/>
                </div>
                <div className="col-12 h-100 align-items-center">
                    <ShareViewSidebarComponent share={props.flopShare} onShareClick={props.onShareClick}/>
                </div>
            </div>
        </div>
    );
};


interface ShareViewSidebarProps {
    onShareClick?: (share: Share) => void;
    share: Share;
}

const ShareViewSidebarComponent = (props: ShareViewSidebarProps) => {
    const formattedPrice = props.share.currentPrice.toLocaleString('de-DE', Formats.currency);

    return (
        <div className="container-fluid hover-btn p-3 box-container" style={{cursor: "pointer"}}
             onClick={() => props.onShareClick?.(props.share)}>
            <div className="row">
                <div className="col-6 text-start">
                    <h3 style={{whiteSpace: "nowrap", overflow: "hidden", textOverflow: "ellipsis"}}>{props.share.companyName}</h3>
                    <h6>ISIN: {props.share.isin}</h6>
                </div>
                <div className="col-6 text-end">
                    <h4>{formattedPrice}</h4>
                    <h4>{coloredArrow(props.share.percentage, true)}</h4>
                </div>
            </div>
        </div>
    );
};

// TODO: Move to appropriate file
export const coloredArrow = (num: number, isPercentage: boolean) => {
    const isPositive = num >= 0;
    return (
        <h4 className={isPositive ? "color-green" : "color-red"}>
            {arrowUpDown(isPositive)}&nbsp;
            {num.toLocaleString('de-DE', Formats.percent)}&nbsp;
            {isPercentage ? '%' : '$'}
        </h4>
    );
}

// TODO: Merge with header arrows
export const arrowUpDown = (isUp: boolean) => {
    if (isUp) {
        return (
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
                 className="bi bi-arrow-up" viewBox="0 0 16 16">
                <path fillRule="evenodd"
                      d="M8 15a.5.5 0 0 0 .5-.5V2.707l3.146 3.147a.5.5 0 0 0 .708-.708l-4-4a.5.5 0 0 0-.708 0l-4 4a.5.5 0 1 0 .708.708L7.5 2.707V14.5a.5.5 0 0 0 .5.5z"/>
            </svg>
        );
    } else {
        return (
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
                 className="bi bi-arrow-down" viewBox="0 0 16 16">
                <path fillRule="evenodd"
                      d="M8 1a.5.5 0 0 1 .5.5v11.793l3.146-3.147a.5.5 0 0 1 .708.708l-4 4a.5.5 0 0 1-.708 0l-4-4a.5.5 0 0 1 .708-.708L7.5 13.293V1.5A.5.5 0 0 1 8 1z"/>
            </svg>
        );
    }
}
