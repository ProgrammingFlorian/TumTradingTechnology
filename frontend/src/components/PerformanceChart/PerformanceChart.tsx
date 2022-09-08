import React from "react";
import {Line, LineChart, ReferenceLine, Tooltip, XAxis, YAxis,} from "recharts";
import {DataPointGraph} from "../../models/DataPointGraph";
import {Formats} from "../../common/Formats";

interface PerformanceChartProps {
    height?: number;
    width?: number;
    dataPointsGraph: DataPointGraph[];
    color: string;
}

/**
 * General Performance Chart.
 */
const PerformanceChart = (props: PerformanceChartProps) => {
    const getDate = (label: any): string => {
        const index = label.valueOf()
        return props.dataPointsGraph[index].date;
    }

    // @ts-ignore
    const CustomizedTooltip = ({active, payload, label}) => {
        if (active && payload?.length > 0) {
            return (
                <div className="custom-tooltip text-white">
                    <h5 className="label">{`${payload[0].value.toLocaleString('de-DE', Formats.currency)}`}</h5>
                    <h5 className="label">{`${getDate(label)}`}</h5>
                </div>
            );
        } else {
            return (
                <br/>
            );
        }
    };

    const calcYofReferenceLine = (): number => {
        if (props.dataPointsGraph.length > 0) {
            return props.dataPointsGraph[props.dataPointsGraph.length - 1]?.price;
        } else {
            return 0;
        }
    };

    return (
        <div id="chart">
            <LineChart width={props.width} height={props.height} data={props.dataPointsGraph}>
                <YAxis hide={true} domain={['dataMin', 'dataMax']}/>
                <XAxis hide={true} reversed={true}/>
                <Tooltip
                    contentStyle={{color: 'white', background: "#232323E5", border: "transparent"}}
                    itemStyle={{color: 'white'}}
                    // @ts-ignore
                    content={<CustomizedTooltip/>}
                />
                <ReferenceLine
                    y={calcYofReferenceLine()} stroke="white"
                    strokeDasharray="3 3"
                />
                <Line type="monotone" dataKey="price" name="date" stroke={props.color} dot={false}/>
            </LineChart>
        </div>
    );
};

export default PerformanceChart;




