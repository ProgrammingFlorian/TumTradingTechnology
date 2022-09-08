import ShareDetailState from "./ShareDetailState";

class InBuyingModeState extends ShareDetailState {
    getAvailableResources(sharesInPortfolio: number, cash: number): string {
        return "Your available money: " + cash.toFixed(2) + " USD";
    }

    getActionDescription(): string {
        return "Please select the number of shares";
    }

    getInputFieldMax(cash: number, price: number, _portfolioQuantity: number): number {
        return cash / price;
    }

    getLeftButtonText(): string {
        return "Back";
    }

    getRightButtonText(): string {
        return "Buy";
    }

    getLeftButtonType(): string {
        return "btn-outline-light";
    }

    getRightButtonType(): string {
        return "btn-outline-success"
    }

    getLeftButtonOutlineColor(): string {
        return "white";
    }

    getRightButtonOutlineColor(): string {
        return "green"
    }

    isBuyState(): boolean {
        return true;
    }
}

export default InBuyingModeState;