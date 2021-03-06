import { AxiosResponse } from "axios";
import { request, authRequest } from "./request";
import cookie from "react-cookies";
import { persistor } from "index";

interface UserApiType {
	signup: (userInfo: {
		email: string;
		password: string;
		username: string;
	}) => Promise<AxiosResponse>;
	isExist: (name: string, value: string) => Promise<AxiosResponse<{ result: boolean }>>;
	signin: (userInfo: { email: string; password: string }) => Promise<AxiosResponse>;
	reissue: (data: { index: number; access_token: string }) => Promise<AxiosResponse>;
	findPassword: (email: string) => Promise<AxiosResponse>;
	logout: () => void;
	checkCode: (code: string) => Promise<AxiosResponse>;
	changePassword: (email: string, password: string) => Promise<AxiosResponse>;
	deleteAccount: () => Promise<AxiosResponse>;
}

const authApi: UserApiType = {
	signup: (userInfo) => request.post("members/join", userInfo),
	isExist: (name, value) => request.post(`members/exist-${name}`, value),
	signin: (userInfo) => request.post("members/login", userInfo),
	reissue: (data) => authRequest.post("members/reissue", data),
	findPassword: (email) => request.post("members/find-pw", email),
	changePassword: (email, password) => request.put("members/change-pw", { email, password }),
	logout: () => {
		cookie.remove("access_token", { path: "/" });
		persistor.purge();
		window.location.replace("/");
	},
	checkCode: (code) => request.put("members/code", { value: code }),
	deleteAccount: () => request.delete("auth/members"),
};

export default authApi;
