import {Share} from "../../models/Share";
import {useNavigate} from "react-router-dom";
import {useEffect, useState} from "react";
import {ShareService} from "../../services/ShareService";
import NavigationBarComponent from "./NavigationBarComponent";
import {AuthenticationService} from "../../services/AuthenticationService";
import {pageDashboard, pageLoginRoute, pageSearch, pageShareDetailRoute} from "../../common/pageRoutes";

const NavigationBarContainer = () => {
    const navigate = useNavigate();
    const [sharesToDisplay, setSharesToDisplay] = useState<Share[]>([]);
    const [marketIsOpen, setMarketIsOpen] = useState<boolean>(false);

    const onLogoClick = () => {
        navigate(pageDashboard());
    };

    const onLogoutClick = () => {
        AuthenticationService.logout();
        navigate(pageLoginRoute());
    };

    const onScrollingShareClick = (share: Share) => {
        const route = pageShareDetailRoute(share.isin);
        navigate(route);
    };

    const loadSharesToDisplay = () => {
        ShareService.getBundle().then((shares) => {
            setSharesToDisplay(shares);
        });
    };

    const updateMarketIsOpen = () => {
        ShareService.getMarketOpen().then((isOpen) => {
            setMarketIsOpen(isOpen);
        })
    };

    const onSearchbarKeyPress = (e: any) => {
        if (e.key === 'Enter') {
            navigate(pageSearch(e.target.value));
        }
    };

    useEffect(() => {
        loadSharesToDisplay()
        updateMarketIsOpen()
        const interval = setInterval(() => {
            loadSharesToDisplay();
            updateMarketIsOpen()
        }, 60000);
        return () => clearInterval(interval);
    }, []);

    return NavigationBarComponent({
        onScrollingElementClick: onScrollingShareClick,
        onLogoClick,
        onLogoutClick,
        onSearchbarKeyPress,
        shares: sharesToDisplay,
        marketIsOpen: marketIsOpen,
        pageBackgroundColor: "#000000",
        greenColor: "#00FF04B5",
        redColor: "#FF0000F2"
    });

};

export default NavigationBarContainer;