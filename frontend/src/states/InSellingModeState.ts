import ShareDetailState from "./ShareDetailState";

class InSellingModeState extends ShareDetailState {
    getAvailableResources(sharesInPortfolio: number, _cash: number): string {
        return "Shares in your portfolio:" + sharesInPortfolio;
    }

    getActionDescription(): string {
        return "Please select the number of shares";
    }

    getInputFieldMax(_cash: number, _price: number, portfolioQuantity: number): number {
        return portfolioQuantity;
    }

    getLeftButtonText(): string {
        return "Back";
    }

    getRightButtonText(): string {
        return "Sell";
    }

    getLeftButtonType(): string {
        return "btn-outline-light";
    }

    getRightButtonType(): string {
        return "btn-outline-danger"
    }

    getLeftButtonOutlineColor(): string {
        return "white";
    }

    getRightButtonOutlineColor(): string {
        return "darkred"
    }

    isBuyState(): boolean {
        return false;
    }
}

export default InSellingModeState;