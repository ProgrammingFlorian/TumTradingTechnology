import React from "react";
import {Share} from "../models/Share";
import ShareViewComponent from "./ShareViewComponent";
import {calculatePortfolioValueOfShare} from "../common/ShareUtils";

interface ShareListProps {
    onShareClick?: (share: Share) => void;
    shares: Share[];
}

const ShareListComponent = (props: ShareListProps) => {
    const sortedShares = props.shares.sort((s1: Share, s2: Share) =>
        calculatePortfolioValueOfShare(s2) - calculatePortfolioValueOfShare(s1));

    return (
        <div className="row gap-3 g-0 pb-xxl-4" style={{overflow: "hidden"}}>
            {sortedShares.map((share: Share, index) => {
                return (
                    <ShareViewComponent share={share} onShareClick={props.onShareClick} animationDelay={`${index / 100}s`}/>
                );
            })}
        </div>
    );
};

export default ShareListComponent;