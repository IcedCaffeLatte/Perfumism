import { useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { useSelector } from "react-redux";
import { RootState } from "store";
import recommendApi from "apis/recommend";
import styled from "styled-components";
import Spinner from "assets/spinner.gif";

function Loading() {
	const [searchParams, setSearchParams] = useSearchParams();
	const navigate = useNavigate();
	const { id } = useSelector((state: RootState) => state.user);

	useEffect(() => {
		getRecommendData();
	}, []);

	const getRecommendData = async () => {
		const answerData = getAnswerData();
		if (answerData) {
			try {
				const res = await recommendApi.surveyRecommend(
					answerData[0],
					answerData[1],
					answerData[2],
					answerData[3],
					answerData[4],
				);
				navigate("/survey/result", {
					state: {
						recommendData: res.data,
					},
				});
			} catch (error) {
				console.log(error);
			}
		} else {
			try {
				const res = await recommendApi.likeBasedRecommend(id);
				navigate("/survey/result", {
					state: {
						recommendData: res.data,
					},
				});
			} catch (error) {
				console.log(error);
			}
		}
	};

	const getAnswerData = () => {
		if (searchParams.get("a1")) {
			const answerData = [
				Number(searchParams.get("a1")),
				Number(searchParams.get("a2")),
				Number(searchParams.get("a3")),
				Number(searchParams.get("a4")),
				Number(searchParams.get("a5")),
			];
			return answerData;
		} else {
			return false;
		}
	};

	return (
		<Container>
			<LoadingImg src={Spinner} />
			<h1>잠시만 기다려 주세요.</h1>
		</Container>
	);
}

const Container = styled.div`
	min-height: 80vh;
	font-size: 1.5rem;
	display: flex;
	flex-direction: column;
	align-items: center;
`;

const LoadingImg = styled.img`
	width: 50rem;
	height: 50rem;
	@media ${(props) => props.theme.mobileXS} {
		width: 100%;
	}
`;

export default Loading;
