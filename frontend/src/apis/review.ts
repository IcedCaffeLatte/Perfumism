import { AxiosResponse } from "axios";
import { request } from "./request";

interface ReviewApiType {
	createReview: (
		review: { grade: number; content: string },
		perfumeId: number,
	) => Promise<AxiosResponse>;
}

const reviewApi: ReviewApiType = {
	createReview: (review, perfumeId) => request.post(`auth/reviews/perfumes/${perfumeId}`, review),
};

export default reviewApi;