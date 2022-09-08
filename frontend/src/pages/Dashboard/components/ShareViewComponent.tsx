import {Share} from "../../../models/Share";
import React from "react";
import "../dashboard.css"
import {calculatePortfolioValueOfShare} from "../../../common/ShareUtils";
import {Formats} from "../../../common/Formats";
import {coloredArrow} from "./DashboardSidebarComponent";

interface ShareViewProps {
    onShareClick?: (share: Share) => void;
    share: Share;
}

const ShareViewComponent = (props: ShareViewProps) => {
    const portfolioValue = calculatePortfolioValueOfShare(props.share).toLocaleString('de-DE', Formats.currency);
    const formattedPrice = props.share.currentPrice.toLocaleString('de-DE', Formats.currency);

    return (
        <div className="container-fluid btn p-3" style={{cursor: "pointer"}}
             onClick={() => props.onShareClick?.(props.share)}>
            <div className="row">
                <div className="col-6 text-start">
                    <h3 className="">{props.share.companyName}</h3>
                    <h5 className="">{formattedPrice}</h5>
                </div>
                <div className="col-6 text-end">
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


