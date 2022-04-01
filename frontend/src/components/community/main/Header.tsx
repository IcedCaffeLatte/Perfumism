import { useNavigate } from "react-router-dom";
import styled from "styled-components";
import { Button, Dropdown } from "../index";

function Header() {
	const navigate = useNavigate();

	const handleCommunityCreateClick = () => {
		navigate("/community/create");
	};

	return (
		<Container>
			<Dropdown />
			<Button onClick={handleCommunityCreateClick}>글쓰기</Button>
		</Container>
	);
}

const Container = styled.div`
	width: 100%;
	display: flex;
	justify-content: space-between;
	margin-top: 8rem;
`;

export default Header;