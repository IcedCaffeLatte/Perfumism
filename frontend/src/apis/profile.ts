import { AxiosResponse } from "axios";
import { imageRequest, request } from "./request";

interface ProfileApiType {
	getFavorites: (currentPage: number) => Promise<AxiosResponse>;
	getUserInfo: () => Promise<AxiosResponse>;
	setUserImage: (formData: FormData) => Promise<AxiosResponse>;
}

const profileApi: ProfileApiType = {
	getFavorites: (currentPage: number) =>
		request.get(`auth/perfumes/likes/my-favorite?page=${currentPage}&size=10`),
	getUserInfo: () => request.get(`auth/members`),
	setUserImage: (formData) => imageRequest.post(`auth/members/img`, formData),
};

export default profileApi;
