import {Outlet, useNavigate} from "react-router-dom";
import {pageLoginRoute} from "../common/pageRoutes";
import {useEffect} from "react";
import {AuthenticationService} from "../services/AuthenticationService";
import NavigationBarContainer from "./NavigationBar/NavigationBarContainer";

/**
 * General page layout where every page is embedded into.
 */
const PageLayout = () => {
    const navigate = useNavigate();
    const loggedIn = AuthenticationService.isLoggedIn();

    useEffect(() => {
        if (!loggedIn) {
            navigate(pageLoginRoute());
        }
        // loggedIn dependency ensures redirect while not logged in or when logging out.
        // If login is faulty in a request, authentication manager deletes the storage and this ensures
        // a redirect to login page.
        // TODO: Sounds nice but doesn't update, so check only occurs on load and not after failed request.
        //  Should be looked into, maybe use context?
        //  Otherwise user has to reload the page for login screen to appear
    }, [navigate, loggedIn]);

    return (
        <>
            <NavigationBarContainer/>
            {   // Only render to page if user is logged in.
                // Redirect happens after the page render, this not only saves performance but also prevents the
                // pages from starting requests while not logged in.
                loggedIn &&
                <Outlet/>
            }
        </>
    )
}

export default PageLayout;