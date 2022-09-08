import {Share} from "../../models/Share";
import {useNavigate} from "react-router-dom";
import {useEffect, useState} from "react";
import {ShareService} from "../../services/ShareService";
import {UserService} from "../../services/UserService";
import DashboardComponent from "./DashboardComponent";
import {DataPointGraph} from "../../models/DataPointGraph";
import {TimeSpan} from "../../models/TimeSpan";
import {calculatePortfolioValueOfShare} from "../../common/ShareUtils";
import {pageShareDetailRoute} from "../../common/pageRoutes";


const DashboardContainer = () => {
    const navigate = useNavigate();

    const [portfolioShares, setPortfolioShares] = useState<Share[]>([]);
    const [topAndFlop, setTopAndFlop] = useState<Share[]>([]);
    const [currentBalance, setCurrentBalance] = useState<number>(0);
    const [remainingCash, setRemainingCash] = useState<number>(0);
    let [differenceInBalance, setDifferenceInBalance] = useState<number>(0);
    const [dataPointsGraph, setDataPointsGraph] = useState<Map<TimeSpan, DataPointGraph[]>>(
        new Map<TimeSpan, DataPointGraph[]>())
    const [activeDataPointGraph, setActiveDataPointGraph] = useState<TimeSpan>(TimeSpan.MAX);

    const onShareClick = (share: Share) => {
        const route: string = pageShareDetailRoute(share.isin);
        navigate(route);
    };

    const onTimeSpanButtonClick = (differenceInBalance: number, timeSpan: TimeSpan) => {
        setActiveDataPointGraph(timeSpan);
        const timeSpanDataGraph = dataPointsGraph.get(timeSpan);
        if (timeSpanDataGraph) {
            setDifferenceInBalance(timeSpanDataGraph[0].price - timeSpanDataGraph[timeSpanDataGraph.length - 1].price);
        } else {
            setDifferenceInBalance(0);
        }
    };

    const loadResults = () => {
        [TimeSpan.DAY, TimeSpan.WEEK, TimeSpan.MONTH, TimeSpan.YEAR, TimeSpan.MAX].map((timeSpan) => {
            ShareService.getShareGraph("portfolio", timeSpan).then((loadedDataPointsGraph) => {
                setDataPointsGraph(dataPointsGraph.set(timeSpan, loadedDataPointsGraph));
            });
        });
        UserService.getCash().then((remainingCash) => {
            setRemainingCash(remainingCash);
        });
        ShareService.getPortfolioShares().then((portfolioShares) => {
            setPortfolioShares(portfolioShares);
            let currentBalance = portfolioShares.map((share: Share) => calculatePortfolioValueOfShare(share))
                .reduce((portfolioValue1: number, portfolioValue2: number) => portfolioValue1 + portfolioValue2, 0);
            UserService.getCash().then((remainingCash) => {
                currentBalance = currentBalance + remainingCash;
                setCurrentBalance(currentBalance);
            });

            ShareService.getTopAndFlopShare().then((topAndFlop) => {
                if (portfolioShares.some(s => (s.companyName === topAndFlop[0].companyName))) {
                    const topShare = portfolioShares.filter(s => s.companyName === topAndFlop[0].companyName);
                    topAndFlop[0] = topShare[0];
                }
                if (portfolioShares.some(s => s.companyName === topAndFlop[1].companyName)) {
                    const flopShare = portfolioShares.filter(s => s.companyName === topAndFlop[1].companyName);
                    topAndFlop[1] = flopShare[0];
                }
                setTopAndFlop(topAndFlop);
            });
        });

        let loadedDataPointsGraph = dataPointsGraph.get(TimeSpan.MAX);
        if (loadedDataPointsGraph) {
            differenceInBalance = loadedDataPointsGraph[0].price - loadedDataPointsGraph[loadedDataPointsGraph.length - 1].price;
            setDifferenceInBalance(differenceInBalance);
        }
    };

    useEffect(() => {
        loadResults();
        const interval = setInterval(() => {
            loadResults();
        }, 60000);
        return () => clearInterval(interval);
    }, []);

    return DashboardComponent({
        onShareClick,
        onTimeSpanButtonClick,
        dataPointsGraph: dataPointsGraph.get(activeDataPointGraph) ?? [],
        portfolioShares,
        topAndFlop,
        currentBalance,
        remainingCash,
        differenceInBalance,
    });
};

export default DashboardContainer;