import networkx as nx

class GraphAnalyzer:
    def __init__(self, graph_data: dict):
        self.graph = nx.DiGraph()
        self.graph.add_nodes_from(n["id"] for n in graph_data["nodes"])
        self.graph.add_edges_from((e["source"], e["target"]) for e in graph_data["edges"])

    def cycles(self):
        return [list(cycle) for cycle in nx.simple_cycles(self.graph)]

    def metrics(self):
        return {
            "components": self.graph.number_of_nodes(),
            "dependencies": self.graph.number_of_edges(),
            "density": round(nx.density(self.graph), 4) if self.graph.number_of_nodes() > 1 else 0,
            "high_coupling": sorted(
                [{"component": n, "connections": self.graph.in_degree(n) + self.graph.out_degree(n)}
                 for n in self.graph.nodes],
                key=lambda x: x["connections"], reverse=True
            )[:5],
        }

    def impact(self, component_id: str):
        if component_id not in self.graph:
            raise KeyError(component_id)
        affected = sorted(nx.descendants(self.graph.reverse(copy=False), component_id))
        return {"component": component_id, "affected_components": affected, "count": len(affected)}
