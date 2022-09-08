import React, {useEffect, useState} from "react";
import PerformanceChart from "./PerformanceChart";
import {TimeSpan} from "../../models/TimeSpan";
import {DataPointGraph} from "../../models/DataPointGraph";

interface PerformanceChartComponentProps {
    dataPointsGraph: DataPointGraph[];
    onTimeSpanButtonClick: (differenceInBalance: number, timespan: TimeSpan) => void;
}

/**
 * Performance Chart in the center of the dashboard.
 */
const PerformanceChartComponent = (props: PerformanceChartComponentProps) => {
    const { width: widthOfChart, height: heightOfChart} = useWindowDimensions()

    // TODO: Use upcoming global color management
    const colorgreen = "#00FF04B5";
    const colorred = "#FF0000F2";
    const colorhover = "#232323E5";
    const textcolor = "#FFFFFFD8";

    const [activeTimeSpan, setActiveTimeSpan] = useState<TimeSpan>(TimeSpan.MAX);
    const [dataPointsGraph, setDataPointsGraph] = useState<DataPointGraph[]>(props.dataPointsGraph)

    const handleClick = (timeSpan: TimeSpan) => {
        props.onTimeSpanButtonClick(0, timeSpan);
        setActiveTimeSpan(timeSpan);
    };

    const determinePerformanceGraphColor = (): string => {
        if (dataPointsGraph !== undefined && dataPointsGraph.length !== 0){
            return dataPointsGraph[0]?.price >= dataPointsGraph[dataPointsGraph.length - 1]?.price ? colorgreen : colorred
        } else {
            return colorgreen;
        }
    }

    const buttonComponent = (timeSpan: TimeSpan) => {
        const active = activeTimeSpan === timeSpan;
        let text: string;

        switch (timeSpan) {
            case TimeSpan.DAY:
                text = 'day';
                break;
            case TimeSpan.WEEK:
                text = 'week';
                break;
            case TimeSpan.MONTH:
                text = 'month';
                break;
            case TimeSpan.YEAR:
                text = 'year';
                break;
            case TimeSpan.MAX:
                text = 'max';
                break;
        }

        return (
            <div className="col-2 container-fluid px-0">
                <div className="container-fluid btn hover-btn px-0" style={{
                    backgroundColor: active ? colorhover : '',
                    color: active ? 'white' : textcolor,
                }}
                     onClick={() => handleClick(timeSpan)}>
                    {text}
                </div>
            </div>
        );
    };

    useEffect(() => {
        setDataPointsGraph(props.dataPointsGraph);
        }, [props.dataPointsGraph]);

    return (
        <div className="container-fluid p-3 box-container anim-opacity" id="performanceChartComponent"
             style={{animationDuration: "0.5s"}}>
            <div className="container-fluid g-0">
                <PerformanceChart height={heightOfChart} width={widthOfChart}
                                  dataPointsGraph={dataPointsGraph} color={determinePerformanceGraphColor()}/>
            </div>
            <div className="row pt-3">
                {[TimeSpan.DAY, TimeSpan.WEEK, TimeSpan.MONTH, TimeSpan.YEAR, TimeSpan.MAX].map(buttonComponent)}
            </div>
        </div>
    );
};

export default PerformanceChartComponent;



function useWindowDimensions() {
    const [width, setWidth] = React.useState(document.getElementById("performanceChartComponent")?.clientWidth);
    const [height, setHeight] = React.useState(document.getElementById("performanceChartComponent")?.clientWidth);

    const updateWidthAndHeight = () => {
        setWidth(calcWidth());
        setHeight(calcHeight());
    };

    //Calculate height based on width
    const calcHeight = (): number => {
        const w = calcWidth();
        if (w != null) {
            if (w < 486) {
                return ((7 / 8) * w) - 50;
            } else if (w < 666) {
                return ((5 / 7) * w) - 50;
            } else if (w < 829) {
                return ((3 / 5) * w) - 50;
            } else {
                return ((3 / 5) * w) - 86;
            }
        } else {
            return 100;
        }
    }

    // Calculate width based on the performanceChartComponents width
    const calcWidth = (): number => {
        const width = document.getElementById("performanceChartComponent")?.clientWidth
        if (width != null) {
            return width - 30;
        } else {
            return 100;
        }
    }

    React.useEffect(() => {
        window.addEventListener("resize", updateWidthAndHeight);
        setWidth(calcWidth());
        setHeight(calcHeight());
    }, [document]);

    return {
        width,
        height
    };
}