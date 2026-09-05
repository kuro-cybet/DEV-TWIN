import networkx as nx
from typing import Dict, List, Any, Optional

class ImpactAnalyzer:
    """
    Analyzes potential change impact on components using graph reachability.
    Distinguishes direct impact (depth 1) from transitive indirect impact (depth > 1).
    """

    def __init__(self, graph_data: Dict[str, Any]):
        self.graph_data = graph_data
        self.nodes_by_id = {n["id"]: n for n in graph_data.get("nodes", [])}
        self.graph = nx.DiGraph()

        for node in graph_data.get("nodes", []):
            self.graph.add_node(
                node["id"],
                name=node.get("name", node["id"]),
                type=node.get("type", "file"),
                file_path=node.get("file_path", node["id"]),
            )

        for edge in graph_data.get("edges", []):
            if edge.get("type") != "contains":
                src = edge["source"]
                tgt = edge["target"]
                if src in self.graph and tgt in self.graph and src != tgt:
                    self.graph.add_edge(src, tgt, type=edge.get("type", "dependency"))

    def analyze(self, component_id: str) -> Dict[str, Any]:
        """
        Calculates direct and indirect change impact when `component_id` is modified.
        In dependency flow (A -> B means A imports/depends on B),
        if B changes, A is directly affected.
        Therefore, we traverse the REVERSE graph from component_id.
        """
        target_id = component_id
        if target_id not in self.graph:
            # Check for path normalization / suffix matching
            norm_id = component_id.replace("\\", "/").strip("/")
            matched = [
                n for n in self.graph.nodes
                if n == norm_id
                or n.endswith(f"/{norm_id}")
                or norm_id.endswith(f"/{n}")
                or n.replace("\\", "/").endswith(norm_id)
                or ("::" not in norm_id and n.split("::")[0].replace("\\", "/").endswith(norm_id))
            ]
            if matched:
                target_id = matched[0]
            else:
                raise KeyError(f"Component '{component_id}' not found in project dependency graph")

        selected_node = self.nodes_by_id.get(target_id, {})
        selected_name = selected_node.get("name", target_id)

        # Reversed graph: edges point from callee/imported to callers/dependents
        rev_graph = self.graph.reverse(copy=True)

        # BFS traversal from target_id to calculate shortest dependency distances
        distances = nx.single_source_shortest_path_length(rev_graph, target_id)

        direct_ids = []
        direct_nodes = []
        indirect_ids = []
        indirect_nodes = []
        affected_all = []

        for node_id, depth in sorted(distances.items(), key=lambda x: x[1]):
            if node_id == target_id:
                continue

            node_info = self.nodes_by_id.get(node_id, {})
            node_payload = {
                "id": node_id,
                "name": node_info.get("name", node_id),
                "type": node_info.get("type", "file"),
                "file_path": node_info.get("file_path", node_id),
                "depth": depth,
                "impact_type": "direct" if depth == 1 else "indirect",
            }

            affected_all.append(node_id)

            if depth == 1:
                direct_ids.append(node_id)
                direct_nodes.append(node_payload)
            else:
                indirect_ids.append(node_id)
                indirect_nodes.append(node_payload)

        # Construct impact subgraph for UI visualization
        impacted_set = set(affected_all) | {target_id}
        subgraph_nodes = [self.nodes_by_id[nid] for nid in impacted_set if nid in self.nodes_by_id]
        subgraph_edges = [
            e for e in self.graph_data.get("edges", [])
            if e["source"] in impacted_set and e["target"] in impacted_set
        ]

        return {
            "selected_component": target_id,
            "selected_component_name": selected_name,
            "total_affected": len(affected_all),
            "direct_count": len(direct_ids),
            "indirect_count": len(indirect_ids),
            "direct_impact": direct_ids,
            "indirect_impact": indirect_ids,
            "direct_impact_nodes": direct_nodes,
            "indirect_impact_nodes": indirect_nodes,
            "affected_components": affected_all,
            "impact_graph": {
                "nodes": subgraph_nodes,
                "edges": subgraph_edges,
            },
            "disclaimer": "POTENTIAL CHANGE IMPACT: Static dependency reachability. Actual runtime impact may vary based on conditional execution flow.",
        }
