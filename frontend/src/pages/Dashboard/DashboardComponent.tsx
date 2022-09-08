import React from "react";
import {Share} from "../../models/Share";
import DashboardSidebarComponent from "./components/DashboardSidebarComponent";
import ShareListComponent from "../../components/ShareListComponent";
import {DataPointGraph} from "../../models/DataPointGraph";
import {TimeSpan} from "../../models/TimeSpan";
import PerformanceChartComponent from "../../components/PerformanceChart/PerformanceChartComponent";

// CSS Files
import "./dashboard.css"
import "../../color.css"

interface DashboardComponentProps {
    onShareClick: (share: Share) => void;
    onTimeSpanButtonClick: (differenceInBalance: number, timespan: TimeSpan) => void;
    dataPointsGraph: DataPointGraph[];
    portfolioShares: Share[];
    topAndFlop: Share[];
    currentBalance: number;
    remainingCash: number;
    differenceInBalance: number;
}

const DashboardComponent = (props: DashboardComponentProps) => {
    return (
        <div className=" bg-black ">
            <div className="container justify-content-center text-white text-opacity-75">
                <div
                    className="row py-4 justify-content-xxl-start justify-content-xl-center justify-content-lg-center g-xxl-3">
                    <div className=" col-sm-12 col-md-12 col-lg-10 col-xl-9 col-xxl-8 order-0 pt-3">
                        <div>
                            <PerformanceChartComponent dataPointsGraph={props.dataPointsGraph}
                                                       onTimeSpanButtonClick={props.onTimeSpanButtonClick}/>
                        </div>
                    </div>
                    <div className="col-sm-12 col-md-12 col-lg-10 col-xl-9 col-xxl-4 order-1 pt-3 sticky-xxl-top">
                        {// TODO: Show skeleton until loaded
                            (props.topAndFlop.length == 2 && props.topAndFlop[0] !== null && props.topAndFlop[1] !== null) ?
                            <DashboardSidebarComponent topShare={props.topAndFlop[0]} flopShare={props.topAndFlop[1]}
                                                       currentBalance={props.currentBalance}
                                                       remainingCash={props.remainingCash}
                                                       differenceInBalance={props.differenceInBalance}
                                                       onShareClick={props.onShareClick}/> :
                            <></>
                        }
                    </div>
                    <div className="col-sm-12 col-md-12 col-lg-10 col-xl-9 col-xxl-8 order-2 pt-3 pt-xxl-0 pb-5">
                        <ShareListComponent shares={props.portfolioShares} onShareClick={props.onShareClick}/>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default DashboardComponent;
