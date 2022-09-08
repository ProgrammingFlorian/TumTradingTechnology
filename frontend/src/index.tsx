import React from 'react';
import ReactDOM, {Root} from 'react-dom/client';
import reportWebVitals from './reportWebVitals';
import {BrowserRouter, Route, Routes} from "react-router-dom";
import PageLayout from "./components/PageLayout";
import ShareDetailPageContainer from "./pages/ShareDetail/ShareDetailPageContainer";
import NotFoundPage from "./pages/NotFoundPage";
import SearchResultContainer from "./pages/SearchResultContainer";
import LoginAndRegistrationContainer from "./pages/LoginAndRegistration/LoginAndRegistrationContainer";
import DashboardContainer from "./pages/Dashboard/DashboardContainer";

// CSS Files
// add bootstrap
import "bootstrap/dist/css/bootstrap.min.css";
import "./index.css";
import "./animations.css";

const root: Root = ReactDOM.createRoot(
    document.getElementById('root') as HTMLElement
);

// add 'bg-black' class to root element for dark background
document.body.classList.add('bg-black');

root.render(
    <React.StrictMode>
        <BrowserRouter>
            <Routes>
                <Route path="/login" element={<LoginAndRegistrationContainer/>}/>
                <Route path="/" element={<PageLayout/>}>
                    <Route index element={<DashboardContainer/>}/>
                    <Route path="share/:id" element={<ShareDetailPageContainer/>}/>
                    <Route path="search/:query" element={<SearchResultContainer/>}/>

                    <Route path="*" element={<NotFoundPage/>}/>
                </Route>
            </Routes>
        </BrowserRouter>
    </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
