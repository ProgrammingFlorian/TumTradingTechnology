abstract class ShareDetailState {
    abstract getAvailableResources(sharesInPortfolio: number, cash: number): string;
    abstract getActionDescription(): string;
    abstract getInputFieldMax(cash:number, price: number, portfolioQuantity: number): number;
    abstract getLeftButtonText(): string;
    abstract getRightButtonText(): string;
    abstract getLeftButtonType(): string;
    abstract getRightButtonType(): string;
    abstract getLeftButtonOutlineColor(): string;
    abstract getRightButtonOutlineColor(): string;
    abstract isBuyState(): boolean;
}
export default ShareDetailState;