import PerfumeList from "components/perfume/PerfumeList";
import useInfiniteScroll from "./hooks/useInfiniteScroll";

function Perfumes() {
	const { setTarget, perfumes, isLoading } = useInfiniteScroll({ type: "perfumes" });

	return (
		<div>
			<PerfumeList perfumes={perfumes} />
			<div ref={setTarget}>{isLoading && <p>Loading..</p>}</div>
		</div>
	);
}

export default Perfumes;
