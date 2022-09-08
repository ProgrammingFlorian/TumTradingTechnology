import {emptyShare, Share} from "../../models/Share";
import {useParams} from "react-router-dom";
import {ShareService} from "../../services/ShareService";
import ShareDetailPageComponent from "./ShareDetailPageComponent";
import {useEffect, useState} from "react";
import {DataPointGraph} from "../../models/DataPointGraph";
import {TimeSpan} from "../../models/TimeSpan";
import {UserService} from "../../services/UserService";
import TransactionState from "../../states/TransactionState";

const ShareDetailPageContainer = () => {

    const {id} = useParams();

    const [share, setShare] = useState<Share>(emptyShare);
    const [isTransactionSuccess, setIsTransactionSuccess] = useState<TransactionState>(TransactionState.PENDING);
    const [cash, setCash] = useState<number>(0);
    const [percentageOfShare, setPercentageOfShare] = useState<number>(0);
    const [currentPrice, setCurrentPrice] = useState<number>(0)
    const [changeInPrice, setChangeInPrice] = useState<number>(0);
    const [dataPointsGraph, setDataPointsGraph] = useState<Map<TimeSpan, DataPointGraph[]>>(
        new Map<TimeSpan, DataPointGraph[]>())
    const [activeDataPointGraph, setActiveDataPointGraph] = useState<TimeSpan>(TimeSpan.MAX);
    let [timeSpanState, setTimeSpanState] = useState<TimeSpan>(TimeSpan.MAX)

    const resetTransactionSuccess = () => {
        setIsTransactionSuccess(TransactionState.PENDING);
    };

    const buyShares = (number: number) => {
        if (share !== null) {
            ShareService.buyShares(share, number).then((success) => {
                if (success) {
                    setIsTransactionSuccess(TransactionState.SUCCESS);
                }
                else {
                    setIsTransactionSuccess(TransactionState.FAILURE);
                }
            });
            loadCash();
        }
    };

    const sellShares = (number: number) => {
        if (share !== null) {
            ShareService.sellShares(share, number).then((success) => {
                if (success) {
                    setIsTransactionSuccess(TransactionState.SUCCESS);
                }
                else {
                    setIsTransactionSuccess(TransactionState.FAILURE);
                }
            })
            loadCash();
        }
    };

    const loadShare = (id: string) => {
        ShareService.getShare(id).then((share) => {
            setShare(share);
        });

        [TimeSpan.DAY, TimeSpan.WEEK, TimeSpan.MONTH, TimeSpan.YEAR, TimeSpan.MAX].map((timeSpan) => {
            ShareService.getShareGraph(id, timeSpan).then((loadedDataPointsGraph) => {
                setDataPointsGraph(dataPointsGraph.set(timeSpan, loadedDataPointsGraph));
            });
        });
    };
    const updateAll = () => {
        if (id) {
            loadShare(id);
        }
        const timeSpanDataGraph = dataPointsGraph.get(activeDataPointGraph);
        if (timeSpanDataGraph) {
            setChangeInPrice(share.currentPrice - timeSpanDataGraph[timeSpanDataGraph.length - 1].price)
        }
        loadCash();
    };

    const loadCash = () => {
        UserService.getCash().then((cash) => {
            setCash(cash);
        });
    };

    const onTimeSpanButtonClick = (balance: number, timeSpan: TimeSpan): void => {
        updateAll();
        setCurrentPrice(share.currentPrice);
        setActiveDataPointGraph(timeSpan);
        timeSpanState = timeSpan;
        setTimeSpanState(timeSpanState)
        const timeSpanDataGraph = dataPointsGraph.get(timeSpan);
        if (timeSpanDataGraph) {
            setChangeInPrice(share.currentPrice - timeSpanDataGraph[timeSpanDataGraph.length - 1].price)
        } else {
            setChangeInPrice(0);
        }
    };

    useEffect(() => {
        updateAll()
        if (id) {
            ShareService.getShareGraph(id, TimeSpan.MAX).then((loadedDataPointsGraph) => {
                if (loadedDataPointsGraph) {
                    setChangeInPrice(share.currentPrice - loadedDataPointsGraph[loadedDataPointsGraph.length - 1].price)
                }
            });
        }

        const interval = setInterval(() => {
            updateAll()
        }, 60000);
        return () => clearInterval(interval);
    }, [id, cash]);

    const getGraph = () => {
        if (dataPointsGraph.get(activeDataPointGraph)) {
            return dataPointsGraph.get(activeDataPointGraph);
        } else {
            updateAll();
            return dataPointsGraph.get(activeDataPointGraph);
        }
    };

    return (ShareDetailPageComponent(
        {
            share,
            buyShares,
            sellShares,
            isTransactionSuccess,
            resetTransactionSuccess,
            cash,
            dataPointsGraph: getGraph() ?? [],
            onTimeSpanButtonClick,
            percentageOfShare,
            changeInPrice,
            updateAll
        }));
}

export default ShareDetailPageContainer;