import {Share} from "../models/Share";
import {useNavigate, useParams} from "react-router-dom";
import SearchResultComponent from "./SearchResultComponent";
import {useEffect, useState} from "react";
import {ShareService} from "../services/ShareService";
import {pageShareDetailRoute} from "../common/pageRoutes";

const SearchResultContainer = () => {
    const navigate = useNavigate();
    const {query} = useParams();
    const [results, setResults] = useState<Share[]>([]);

    const onShareClick = (share: Share) => {
        const route: string = pageShareDetailRoute(share.isin);
        navigate(route);
    }

    const loadResults = (query: string) => {
        ShareService.search(query).then((shares) => {
            setResults(shares);
        });
    };

    useEffect(() => {
        if (query) {
            loadResults(query);
        }
    }, [query]);

    return SearchResultComponent({
        onShareClick,
        query: query ?? '',
        shares: results
    });

};

export default SearchResultContainer;