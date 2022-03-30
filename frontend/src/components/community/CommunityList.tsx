import { CommunityListItem } from "components/community";
import styled from "styled-components";

function CommunityList() {
	const articleData = [
		{
			articleList: [
				{
					article_id: 1,
					member_id: 1,
					member_name: "우사앙주운",
					subject: "TALK",
					title: "제목입니다",
					content: "내용입니다",
					createAt: "2022-3-13 14:59:51",
					updateAt: "2023-4-14 14:59:51",
					deleteAt: "2023-4-15 14:59:51",
				},
			],
			total_page_count: 2,
			current_page_count: 1,
		},
		{
			articleList: [
				{
					article_id: 2,
					member_id: 1,
					member_name: "우사앙주운",
					subject: "TALK",
					title: "제목입니다",
					content: "내용입니다",
					createAt: "2022-3-13 14:59:51",
					updateAt: "2023-4-14 14:59:51",
					deleteAt: "2023-4-15 14:59:51",
				},
			],
			total_page_count: 4,
			current_page_count: 1,
		},
		{
			articleList: [
				{
					article_id: 3,
					member_id: 1,
					member_name: "우사앙주운",
					subject: "TALK",
					title: "제목입니다",
					content: "내용입니다",
					createAt: "2022-3-13 14:59:51",
					updateAt: "2023-4-14 14:59:51",
					deleteAt: "2023-4-15 14:59:51",
				},
			],
			total_page_count: 1,
			current_page_count: 1,
		},
	];

	return (
		<Container>
			<table>
				<tr>
					<th>번호</th>
					<th>말머리</th>
					<th>제목</th>
					<th>작성자</th>
					<th>작성일</th>
					<th>조회수</th>
				</tr>
				{articleData.map((articleItem, idx) => (
					<CommunityListItem articleItem={articleItem} key={idx} />
				))}
			</table>
		</Container>
	);
}

const Container = styled.div`
	font-size: 2rem;
	width: 100%;
`;

export default CommunityList;
