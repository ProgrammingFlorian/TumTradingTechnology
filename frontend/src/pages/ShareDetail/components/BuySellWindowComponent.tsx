import {Share} from "../../../models/Share";
import {ChangeEvent, useState} from "react";
import ShareDetailState from "../../../states/ShareDetailState";
import TransactionState from "../../../states/TransactionState";

interface BuySellWindowProps {
    share: Share;
    setIsInOverview: (isInOverview: boolean) => void;
    buySellState: ShareDetailState;
    buyShares: (number: number) => void;
    sellShares: (number: number) => void;
    cash: number;
    isTransactionSuccess: TransactionState;
    resetTransactionSuccess: () => void;
    updateAll: () => void;
}

const BuySellWindowComponent = (props: BuySellWindowProps) => {
    const [transactionAmount, setTransactionAmount] = useState<number>(1);
    const [priceToPay, setPriceToPay] = useState<number>(props.share.currentPrice);
    const [isTransactionSubmitted, setIsTransactionSubmitted] = useState<boolean>(false);

    const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
        const amount = Number(e.target.value);
        if (amount % 1 === 0 && amount > 0 && amount * props.share.currentPrice <= props.cash) {
            setTransactionAmount(amount);
            setPriceToPay(Math.round(amount * props.share.currentPrice * 100) / 100);
        }
    }

    if (isTransactionSubmitted) {
        return ResponseWindow(props.isTransactionSuccess, transactionAmount, props.buySellState, props.setIsInOverview, props.resetTransactionSuccess, setIsTransactionSubmitted, props.updateAll);

    } else {
        return (
            <div className="container-fluid text-white">
                <div className="p-1"/>
                <div>
                    {props.buySellState.getAvailableResources(props.share.portfolioQuantity, props.cash)}
                </div>
                <div className="p-1"/>

                <div className="pt-1">
                    {props.buySellState.getActionDescription()}
                </div>

                <input className="form-control border-light text-light" type="number" name="number" min="1"
                       step="1" defaultValue="1"
                       max={props.buySellState.getInputFieldMax(props.cash, props.share.currentPrice, props.share.portfolioQuantity)}
                       onChange={handleChange}
                       style={{backgroundColor: "#2d2d2d"}}/>

                <div className="p-3"/>

                <div className="text-center py-1">
                    {transactionAmount} shares for {priceToPay.toFixed(2)} USD
                </div>

                <div className=" text-center py-3">
                    <div className="py-1">
                        {UpperButton(props.buyShares, props.sellShares, props.buySellState, transactionAmount, setIsTransactionSubmitted)}
                    </div>
                    <div className="py-1">
                        {LowerButton(props.buySellState, props.setIsInOverview, props.resetTransactionSuccess, setIsTransactionSubmitted, props.updateAll)}
                    </div>

                </div>
            </div>
        );
    }
};


const LowerButton = (buySellState: ShareDetailState, setIsInOverview: (newIsInOverview: boolean) => void, resetTransactionSuccess: () => void, setTransactionSubmitted: (value: boolean) => void, updateAll: () => void) => {
    const onClick = () => {
        updateAll();
        setIsInOverview(true);
        setTransactionSubmitted(false);
        resetTransactionSuccess();
    }
    const className = "backButton btn " + buySellState.getLeftButtonType() + " w-100";
    const borderColor = buySellState.getLeftButtonOutlineColor();
    return (
        <button className={className} onClick={onClick} style={{
            borderColor: borderColor, minWidth: 130,
            minHeight: 40, borderWidth: "medium"
        }}>
            {buySellState.getLeftButtonText()}
        </button>
    );
};

const UpperButton = (buyShares: (number: number) => void, sellShares: (number: number) => void, buySellState: ShareDetailState, amount: number, setTransactionSubmitted: (value: boolean) => void) => {
    const onClick = () => {
        if (buySellState.isBuyState()) {
            buyShares(amount);
            setTransactionSubmitted(true);
        } else {
            sellShares(amount);
            setTransactionSubmitted(true);
        }
    }
    const className = "btn " + buySellState.getRightButtonType() + " w-100"
    const borderColor = buySellState.getRightButtonOutlineColor();

    return (
        <button className={className} onClick={onClick} style={{
            color: "white", borderColor: borderColor, minWidth: 130,
            minHeight: 40, borderWidth: "medium"
        }}>
            {buySellState.getRightButtonText()}
        </button>
    );
};

const ResponseWindow = (isTransactionSuccess: TransactionState, number: number, buySellState: ShareDetailState, setIsInOverview: (newIsInOverview: boolean) => void, resetTransactionSuccess: () => void, setTransactionSubmitted: (value: boolean) => void, updateAll: () => void) => {
    let text;
    let className;

    if (isTransactionSuccess == TransactionState.PENDING) {
        text = "Wait for execution of your transaction!";
        className = "text-light py-1";
    } else if (isTransactionSuccess == TransactionState.SUCCESS) {
        text = "Your transaction of " + number + " shares was successful!";
        className = "text-success py-1";
    } else {
        text = "Unfortunately your transaction of " + number + " shares was NOT successful!";
        className = "text-danger py-1";
    }

    return (
        <div className="container-fluid text-center">
            <div className="row">
                <div className="col text-center justify-content-center">
                    <div className="p-3"/>
                    <div className={className}>{text}</div>
                    {LowerButton(buySellState, setIsInOverview, resetTransactionSuccess, setTransactionSubmitted, updateAll)}
                </div>
            </div>
        </div>
    );
};

export default BuySellWindowComponent;