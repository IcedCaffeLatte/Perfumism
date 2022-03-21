import perfumeApi from "apis/perfume";
import PerfumeList from "components/perfume/PerfumeList";
import ReviewCreateForm from "components/review/ReviewCreateForm";
import ReviewList from "components/review/ReviewList";
import { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import styled from "styled-components";

type PerfumeDataType = {
	perfume_id: number;
	perfume_name: string;
	brand: {
		brand_id: number;
		brand_name: string;
	};
	image: string;
	launch_year: number;
	average_grade: number;
	top_notes: string;
	middle_notes: string | null;
	base_notes: string | null;
	total_survey: number;
	longevity: string;
	sillage: string;
	accords: AccordType[];
	similar_perfume: [];
};

type AccordType = {
	accord_id: number;
	kor_name: string;
	eng_name: string;
};

function PerfumeDetail() {
	const location = useLocation();
	const perfumeId = 1;
	// const perfumeId = location.state.perfumeId;
	const [perfumeData, setPerfumeData] = useState<PerfumeDataType | null>(null);

	useEffect(() => {
		getPerfume();
	}, []);

	const getPerfume = async () => {
		await perfumeApi.getPerfume(perfumeId).then((res) => setPerfumeData(res.data));
	};

	return (
		perfumeData && (
			<Container>
				<PerfumeMainInfo>
					<img src={`https://fimgs.net/mdimg/perfume/375x500.${perfumeData.image.slice(2)}`} />
					<h1>{perfumeData.perfume_name}</h1>
					<h3>{perfumeData.launch_year}</h3>
					<h3>{perfumeData.brand.brand_name}</h3>
					<h3>{perfumeData.average_grade}</h3>
					<p>main accords</p>
					<ul>
						{perfumeData.accords.map((accord) => (
							<li key={accord.accord_id}>{accord.eng_name}</li>
						))}
					</ul>
				</PerfumeMainInfo>
				<PerfumeSubInfo>
					<p>{perfumeData.top_notes}</p>
					<p>{perfumeData.middle_notes}</p>
					<p>{perfumeData.base_notes}</p>
					<p>{perfumeData.longevity}</p>
					<p>{perfumeData.sillage}</p>
				</PerfumeSubInfo>
				<Recommendation>
					<p>000과 비슷한 향수</p>
					<PerfumeList perfumes={perfumeData.similar_perfume} />
					<p>브랜드의 다른 향수</p>
				</Recommendation>
				<ReviewCreateForm perfumeId={perfumeId} />
				<ReviewList perfumeId={perfumeId} />
			</Container>
		)
	);
}

const Container = styled.div``;
const PerfumeMainInfo = styled.div``;
const PerfumeSubInfo = styled.div``;
const Recommendation = styled.div``;

export default PerfumeDetail;