import { useSearchParams, useLocation } from "react-router-dom";
import SurveyItem from "components/recommend/SurveyItem";
interface queryProps {
	queryString: string;
}

function Survey() {
	const [searchParams, setSearchParams] = useSearchParams();
	const page = searchParams.get("page");
	const queryString = useLocation().search;
	console.log(queryString);

	return (
		<div>
			{page === "1" ? <SurveyItem queryString={queryString} /> : null}
			{/* {page === "2" ? <SurveyItem {...queryString} /> : null}
			{page === "3" ? <SurveyItem {...queryString} /> : null}
			{page === "4" ? <SurveyItem {...queryString} /> : null}
			{page === "5" ? <SurveyItem {...queryString} /> : null} */}
		</div>
	);
}

export default Survey;
