import styled from "styled-components";

interface SectionProps {
	image: string;
}

const Section = styled.section<SectionProps>`
	position: relative;
	height: 80rem;
	background: ${(props) => `url(${props.image}) no-repeat top`};
`;

export default Section;