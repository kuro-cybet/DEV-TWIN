import networkx as nx
from typing import Dict, List, Any, Optional

class DependencyAnalyzer:
    """
    NetworkX-based dependency graph analyzer.
    Calculates coupling, stability, graph density, centrality, cycles, and critical hubs.
    """

    def __init__(self, graph_data: Dict[str, Any]):
        self.graph_data = graph_data
        self.graph = nx.DiGraph()

        # Build directed graph from nodes and edges (ignoring purely containment edges for dependency flow)
        for node in graph_data.get("nodes", []):
            self.graph.add_node(
                node["id"],
                name=node.get("name", node["id"]),
                type=node.get("type", "file"),
                file_path=node.get("file_path", node["id"]),
                parent=node.get("parent"),
            )

        for edge in graph_data.get("edges", []):
            # Only include dependency-like relationships (import, inheritance, call) in the dependency flow
            if edge.get("type") != "contains":
                src = edge["source"]
                tgt = edge["target"]
                if src in self.graph and tgt in self.graph and src != tgt:
                    self.graph.add_edge(
                        src,
                        tgt,
                        id=edge.get("id", f"{src}->{tgt}"),
                        type=edge.get("type", "dependency"),
                        confidence=edge.get("confidence", 1.0),
                    )

    def cycles(self) -> Dict[str, Any]:
        """
        Detects circular dependencies in the directed graph.
        Returns full cycle paths and involved components.
        """
        raw_cycles = list(nx.simple_cycles(self.graph))
        # Format cycles with return loop [A, B, C, A] for clear display
        formatted_cycles = [list(c) for c in raw_cycles]
        involved = sorted(list(set(node for c in raw_cycles for node in c)))

        count = len(formatted_cycles)
        if count == 0:
            severity = "NONE"
        elif count <= 2:
            severity = "LOW"
        elif count <= 5:
            severity = "MEDIUM"
        elif count <= 10:
            severity = "HIGH"
        else:
            severity = "CRITICAL"

        return {
            "count": count,
            "cycles": formatted_cycles,
            "involved_components": involved,
            "severity": severity,
        }

    def metrics(self) -> Dict[str, Any]:
        """
        Calculates dependency metrics, coupling breakdown, instability index, and critical components.
        """
        num_nodes = self.graph.number_of_nodes()
        num_edges = self.graph.number_of_edges()

        density = round(nx.density(self.graph), 4) if num_nodes > 1 else 0.0
        avg_deps = round(num_edges / num_nodes, 2) if num_nodes > 0 else 0.0

        # Centrality metrics
        betweenness = nx.betweenness_centrality(self.graph) if num_nodes > 1 else {n: 0.0 for n in self.graph.nodes}
        
        # Calculate Coupling for every component
        coupling_items = []
        coupling_dist = {"LOW": 0, "MEDIUM": 0, "HIGH": 0}

        for node_id in self.graph.nodes:
            node_data = self.graph.nodes[node_id]
            incoming = self.graph.in_degree(node_id)  # Afferent coupling (Ca)
            outgoing = self.graph.out_degree(node_id)  # Efferent coupling (Ce)
            total = incoming + outgoing

            # Martin's Instability metric: I = Ce / (Ca + Ce)
            instability = round(outgoing / total, 2) if total > 0 else 0.0

            if total <= 2:
                level = "LOW"
                explanation = f"Low coupling ({incoming} in, {outgoing} out). Isolated or leaf component with minimal ripple effect."
            elif total <= 5:
                level = "MEDIUM"
                explanation = f"Moderate coupling ({incoming} in, {outgoing} out). Balanced dependencies across standard application flow."
            else:
                level = "HIGH"
                explanation = f"High coupling ({incoming} in, {outgoing} out). Highly connected component; modifications risk broad regression."

            coupling_dist[level] += 1

            coupling_items.append({
                "component": node_id,
                "name": node_data.get("name", node_id),
                "type": node_data.get("type", "file"),
                "incoming": incoming,
                "outgoing": outgoing,
                "total": total,
                "instability": instability,
                "coupling_level": level,
                "explanation": explanation,
            })

        # Sort coupling by total connections descending
        coupling_items.sort(key=lambda x: x["total"], reverse=True)

        # Critical Components Identification
        critical_components = self._identify_critical_components(betweenness)

        return {
            "components": num_nodes,
            "dependencies": num_edges,
            "density": density,
            "density_percentage": round(density * 100, 2),
            "avg_dependencies_per_component": avg_deps,
            "high_coupling": coupling_items[:15],
            "critical_components": critical_components,
            "coupling_distribution": coupling_dist,
        }

    def _identify_critical_components(self, betweenness: Dict[str, float]) -> List[Dict[str, Any]]:
        """
        Ranks components by structural criticality using a weighted score of
        betweenness centrality and in-degree (dependability/fan-in).
        """
        results = []
        for node_id in self.graph.nodes:
            node_data = self.graph.nodes[node_id]
            incoming = self.graph.in_degree(node_id)
            outgoing = self.graph.out_degree(node_id)
            bw = betweenness.get(node_id, 0.0)

            # Combined criticality score (0.0 to 10.0 scale)
            score = round((incoming * 1.5) + (outgoing * 0.5) + (bw * 10.0), 2)

            if score >= 5.0 or incoming >= 4:
                level = "HIGH"
                explanation = f"Critical architectural hub: {incoming} components depend directly on this, high routing betweenness ({round(bw, 3)})."
            elif score >= 2.0 or incoming >= 2:
                level = "MEDIUM"
                explanation = f"Moderate criticality: {incoming} incoming and {outgoing} outgoing connections."
            else:
                level = "LOW"
                explanation = "Standard leaf or utility component with low systemic risk."

            results.append({
                "component": node_id,
                "name": node_data.get("name", node_id),
                "type": node_data.get("type", "file"),
                "score": score,
                "level": level,
                "incoming": incoming,
                "outgoing": outgoing,
                "betweenness": round(bw, 4),
                "explanation": explanation,
            })

        results.sort(key=lambda x: x["score"], reverse=True)
        return results[:10]
