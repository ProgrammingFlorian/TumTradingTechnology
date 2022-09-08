import {Share} from "../models/Share";
import ShareListComponent from "../components/ShareListComponent";

interface SearchResultComponentProps {
    onShareClick: (share: Share) => void;

    query: string;
    shares: Share[];
}

const SearchResultComponent = (props: SearchResultComponentProps) => {
    return (
        <div className="container-fluid p-0">
            <div className="container-fluid p-0" style={{backgroundColor: "transparent"}}>
                <h3 className="text-white mb-3 px-3 py-2">Suchergebnisse für {props.query}:</h3>
            </div>
            <div className="container">
                <ShareListComponent shares={props.shares} onShareClick={props.onShareClick}/>
            </div>
        </div>
    );
};

export default SearchResultComponent;