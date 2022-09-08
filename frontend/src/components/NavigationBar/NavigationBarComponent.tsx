import Marquee from "react-fast-marquee";
import searchIcon from "../../resources/icons/search.svg"
import closeIcon from "../../resources/icons/close.svg"
import marketOpen from "../../resources/icons/marketOpen.svg"
import marketClosed from "../../resources/icons/marketClosed.svg"
import logo from "../../resources/icons/TTT.svg"
import {useState} from "react";
import {Share} from "../../models/Share";

// CSS Files
import "./NavigationBarComponent.css"
import {Formats} from "../../common/Formats";

interface NavigationBarProps {
    onScrollingElementClick?: (share: Share) => void;
    onLogoClick?: () => void;
    onLogoutClick?: () => void;
    onSearchbarKeyPress?: (e: any) => void;

    shares: Share[];
    marketIsOpen: boolean;
    pageBackgroundColor: string;
    greenColor: string;
    redColor: string;
}

interface ScrollingStocksProps {
    onScrollingElementClick?: (share: Share) => void;
    shares: Share[];
    backgroundColor: [number, number, number];
    greenColor: string;
    redColor: string;
}

interface StockScrollElementProps {
    onClick?: (share: Share) => void;
    share: Share;
    greenColor: string;
    redColor: string;
}

const NavigationBarComponent = (props: NavigationBarProps) => {
    return (
        <div className="container-fluid" style={{height: 65, marginBottom: 10}}>
            <UpperElement shares={props.shares}
                          marketIsOpen={props.marketIsOpen}
                          onLogoClick={props.onLogoClick}
                          onLogoutClick={props.onLogoutClick}
                          onScrollingElementClick={props.onScrollingElementClick}
                          onSearchbarKeyPress={props.onSearchbarKeyPress}
                          pageBackgroundColor={props.pageBackgroundColor}
                          greenColor={props.greenColor}
                          redColor={props.redColor}/>
            {/*Just on small devices*/}
            <LowerElement shares={props.shares}
                          marketIsOpen={props.marketIsOpen}
                          onLogoClick={props.onLogoClick}
                          onLogoutClick={props.onLogoutClick}
                          onScrollingElementClick={props.onScrollingElementClick}
                          pageBackgroundColor={props.pageBackgroundColor}
                          greenColor={props.greenColor}
                          redColor={props.redColor}/>
        </div>
    );
};

const UpperElement = (props: NavigationBarProps) => {
    return (
        <nav className="shadow navbar navbar-expand-sm fixed-top mb-5"
             style={{position: "fixed", top: 0, height: 65, backgroundColor: "#000000"}}>
            <div className="container-fluid">
                <a className="navbar-brand text-white" onClick={props.onLogoClick} style={{cursor: "pointer"}}>
                    <img alt={"Logo"} src={logo} height={50} width={50}/>
                </a>
                <img alt={"marketStatusIcon"} src={props.marketIsOpen ? marketOpen : marketClosed} height={45}
                     width={45}/>
                {/*Just on large devices*/}
                <div className="container-fluid d-none d-sm-block">
                    <CenterBar shares={props.shares}
                               marketIsOpen={props.marketIsOpen}
                               onScrollingElementClick={props.onScrollingElementClick}
                               onSearchbarKeyPress={props.onSearchbarKeyPress}
                               pageBackgroundColor={props.pageBackgroundColor}
                               greenColor={props.greenColor}
                               redColor={props.redColor}/>
                </div>
                {/*Just on small devices*/}
                <div className="col-4 d-block d-sm-none">
                    <SearchView handleKeyPress={props.onSearchbarKeyPress}/>
                </div>
                <ul className="navbar-nav ms-auto">
                    <li className="nav-item">
                        <a className="nav-link text-white user-select-none" style={{cursor: "pointer"}}
                           onClick={props.onLogoutClick}>
                            Logout
                        </a>
                    </li>
                </ul>
            </div>
        </nav>
    );
}

const LowerElement = (props: NavigationBarProps) => {
    return (
        <div className="container-fluid d-block d-sm-none"
             style={{position: "sticky", marginTop: 65, height: 50, backgroundColor: props.pageBackgroundColor}}>
            <ScrollingStocks onScrollingElementClick={props.onScrollingElementClick}
                             shares={props.shares}
                             backgroundColor={[0, 0, 0]}
                             greenColor={props.greenColor}
                             redColor={props.redColor}/>
        </div>
    );
}

const CenterBar = (props: NavigationBarProps) => {
    const [barToShow, setBarToShow] = useState<boolean>(false);

    const toggleCenterBar = () => {
        setBarToShow(!barToShow);
    };

    return (
        <div className="navbar-collapse show">
            <button className="btn btn-search" onClick={toggleCenterBar} type="button" style={{boxShadow: "none"}}>
                <img alt="Search Button" src={barToShow ? closeIcon : searchIcon}/>
            </button>
            {barToShow ?
                <SearchView handleKeyPress={props.onSearchbarKeyPress}/> :
                <StartView onScrollingElementClick={props.onScrollingElementClick}
                           shares={props.shares}
                           marketIsOpen={props.marketIsOpen}
                           pageBackgroundColor={props.pageBackgroundColor}
                           greenColor={props.greenColor}
                           redColor={props.redColor}/>
            }
        </div>
    );
}

const StartView = (props: NavigationBarProps) => {
    // TODO: Animation only works if scrollingstonks loads instantly
    return (
        <div className="show navbar-collapse anim-opacity">
            <ScrollingStocks onScrollingElementClick={props.onScrollingElementClick}
                             shares={props.shares}
                             backgroundColor={[0, 0, 0]}
                             greenColor={props.greenColor}
                             redColor={props.redColor}/>
        </div>
    );
}

interface SearchViewProps {
    handleKeyPress?: (e: any) => void;
}

const SearchView = (props: SearchViewProps) => {
    return (
        <div className="show navbar-collapse"> {/*TODO execute search*/}
            <div className="container-fluid">
                <input type="text" className="HSearchBar form-control" placeholder="Search"
                       onKeyDown={props.handleKeyPress}/>
            </div>
        </div>
    );
}

const ScrollingStocks = (props: ScrollingStocksProps) => {
    return (
        <Marquee pauseOnHover={true} gradientColor={props.backgroundColor} speed={50}>
            <ul className="navbar-nav me-auto">
                <div className={"row"}>
                    {props.shares.map((share: Share) => {
                        return (
                            <StockScrollElement share={share} onClick={props.onScrollingElementClick}
                                                greenColor={props.greenColor}
                                                redColor={props.redColor}/>
                        );
                    })}
                </div>
            </ul>
        </Marquee>
    );
}

const StockScrollElement = (props: StockScrollElementProps) => {
    const isPositive = props.share.percentage >= 0;
    let percentage = props.share.percentage.toLocaleString("de-DE",
        props.share.percentage >= 100 ? Formats.percentBigNumber : Formats.percent);
    return (
        <div className={"ms-5 col"}
             style={{color: isPositive ? props.greenColor : props.redColor, minWidth: 75, cursor: "pointer"}}
             onClick={() => props.onClick?.(props.share)}>
            <div className={"row user-select-none"}>
                {isPositive ?
                    `↑${props.share.symbol}` :
                    `↓${props.share.symbol}`
                }
            </div>
            <div className={"row user-select-none"}>
                {isPositive ? '+' : ''}{percentage} %
            </div>
        </div>
    );
}

export default NavigationBarComponent;