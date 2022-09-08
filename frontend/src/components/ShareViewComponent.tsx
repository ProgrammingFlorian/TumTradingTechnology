import {Share} from "../models/Share";
import React from "react";
import {calculatePortfolioValueOfShare} from "../common/ShareUtils";
import {Formats} from "../common/Formats";
import {coloredArrow} from "../pages/Dashboard/components/DashboardSidebarComponent";

// CSS Files
import "./ShareView.css"

interface ShareViewProps {
    onShareClick?: (share: Share) => void;

    share: Share;
    animationDelay?: string;
}

const ShareViewComponent = (props: ShareViewProps) => {
    const portfolioValue = calculatePortfolioValueOfShare(props.share).toLocaleString('de-DE', Formats.currency);
    const formattedPrice = props.share.currentPrice.toLocaleString('de-DE', Formats.currency);

    return (
        <div className="anim-slide-right container-fluid hover-btn box-container p-3" style={{
            cursor: "pointer",
            animationDelay: props.animationDelay ?? "0"
        }}
             onClick={() => props.onShareClick?.(props.share)}>
            <div className="row">
                <div className="col-6 text-start text-white">
                    <h3>{props.share.companyName}</h3>
                    <h5 className="text-opacity-50 text-white">{formattedPrice}</h5>
                </div>
                <div className="col-6 text-end text-white">
                    <h4>
                        <span className="portfolioQuantity px-3">
                            {props.share.portfolioQuantity}x
                        </span>
                        &nbsp;
                        {portfolioValue}
                    </h4>
                    <h4>{coloredArrow(props.share.percentage, true)}</h4>
                </div>
            </div>
        </div>
    );
}

export default ShareViewComponent;


