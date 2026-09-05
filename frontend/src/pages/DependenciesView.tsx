import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import {
  GitFork,
  AlertTriangle,
  Activity,
  CheckCircle2,
  RefreshCw,
  Search,
  SlidersHorizontal,
} from "lucide-react";
import { Layout } from "../components/Layout";
import { MetricCard } from "../components/MetricCard";
import { InteractiveGraph } from "../components/InteractiveGraph";
import { EmptyState, ErrorBanner, LoadingSkeleton } from "../components/EmptyState";
import { projectsApi } from "../services/api/projects";
import { dependenciesApi } from "../services/api/dependencies";
import {
  Project,
  DependencyMetrics,
  CyclesData,
  GraphData,
  CouplingItem,
} from "../services/api/types";

export function DependenciesView() {
  const { projectId = "" } = useParams();
  const [project, setProject] = useState<Project | null>(null);
  const [metrics, setMetrics] = useState<DependencyMetrics | null>(null);
  const [cycles, setCycles] = useState<CyclesData | null>(null);
  const [graphData, setGraphData] = useState<GraphData | null>(null);
  const [filterLevel, setFilterLevel] = useState<string>("all");
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const loadData = async () => {
    if (!projectId) return;
    try {
      setLoading(true);
      setError(null);
      const proj = await projectsApi.get(projectId);
      setProject(proj);

      if (proj.status === "analyzed") {
        const [m, c, g] = await Promise.all([
          dependenciesApi.getMetrics(projectId),
          dependenciesApi.getCycles(projectId),
          dependenciesApi.getGraph(projectId),
        ]);
        setMetrics(m);
        setCycles(c);
        setGraphData(g);
      }
    } catch (err: any) {
      setError(err.message || "Failed to load dependencies.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [projectId]);

  const filteredCoupling: CouplingItem[] = (metrics?.high_coupling || []).filter((item) => {
    const matchesSearch =
      search.trim() === "" ||
      item.name.toLowerCase().includes(search.toLowerCase()) ||
      item.component.toLowerCase().includes(search.toLowerCase());
    const matchesLevel = filterLevel === "all" || item.coupling_level === filterLevel;
    return matchesSearch && matchesLevel;
  });

  return (
    <Layout activeProject={project} onRefreshProject={loadData}>
      <div className="dependencies-page-wrap">
        <div className="view-page-header">
          <div>
            <span className="view-eyebrow">MODULE 2 — DEPENDENCY INTELLIGENCE</span>
            <h1 className="view-title">Dependency Analysis & Coupling</h1>
            <p className="view-subtitle">
              Calculates incoming/outgoing coupling, Martin's instability index, and circular dependency loops.
            </p>
          </div>
        </div>

        {error && <ErrorBanner message={error} onRetry={loadData} />}

        {loading ? (
          <LoadingSkeleton />
        ) : !project || project.status !== "analyzed" || !metrics ? (
          <EmptyState
            title="Analysis Required"
            description="Run analysis to calculate dependency matrices and detect architectural cycles."
            actionLabel="Run Analysis"
            onAction={async () => {
              await projectsApi.analyze(projectId);
              await loadData();
            }}
          />
        ) : (
          <>
            {/* Dependency Metrics Summary Row */}
            <div className="metrics-grid">
              <MetricCard
                label="Direct Dependencies"
                value={metrics.dependencies}
                subtext="Directed graph edges"
                tooltip="Direct import and inheritance connections discovered between components."
                icon={<GitFork size={20} />}
              />

              <MetricCard
                label="Circular Dependencies"
                value={cycles?.count ?? 0}
                subtext={cycles?.count === 0 ? "Zero cycles" : `${cycles?.count} cycle paths`}
                badge={{
                  text: cycles?.severity || "NONE",
                  variant:
                    cycles?.severity === "NONE"
                      ? "success"
                      : cycles?.severity === "LOW"
                      ? "warning"
                      : "danger",
                }}
                tooltip="Loops in the dependency graph where components mutually depend on each other."
                icon={<AlertTriangle size={20} />}
              />

              <MetricCard
                label="Graph Density"
                value={`${metrics.density_percentage}%`}
                subtext="Relative connectivity"
                tooltip="Density = E / (V * (V - 1)). Higher density indicates tighter cross-coupling."
                icon={<Activity size={20} />}
              />

              <MetricCard
                label="Avg Fan-Out / Fan-In"
                value={metrics.avg_dependencies_per_component}
                subtext="Connections per component"
                tooltip="Average number of dependencies associated with each software component."
                icon={<SlidersHorizontal size={20} />}
              />
            </div>

            {/* Circular Dependencies Card */}
            <div className="card-panel">
              <div className="panel-header">
                <div>
                  <h3 className="panel-title">Circular Dependencies Diagnostic</h3>
                  <p className="panel-desc">
                    Mutual recursion or cyclic import chains detected using NetworkX cycle algorithms
                  </p>
                </div>
                <span className={`pill-badge badge-${cycles?.severity.toLowerCase() || "none"}`}>
                  {cycles?.severity} SEVERITY
                </span>
              </div>

              {cycles && cycles.cycles.length > 0 ? (
                <div className="cycles-list">
                  {cycles.cycles.map((path, idx) => (
                    <div key={idx} className="cycle-path-card">
                      <div className="cycle-path-header">
                        <span className="cycle-idx">Cycle #{idx + 1}</span>
                        <span className="cycle-length">{path.length} hops</span>
                      </div>
                      <div className="cycle-chain">
                        {path.map((nodeId, i) => (
                          <React.Fragment key={i}>
                            <span className="cycle-node-tag">{nodeId}</span>
                            <span className="cycle-arrow">→</span>
                          </React.Fragment>
                        ))}
                        <span className="cycle-node-tag cycle-loop-end">{path[0]}</span>
                      </div>
                    </div>
                  ))}

                  <div className="cycle-involved-section">
                    <span className="involved-title">All Involved Components:</span>
                    <div className="involved-tags-wrap">
                      {cycles.involved_components.map((c) => (
                        <span key={c} className="involved-tag">
                          {c}
                        </span>
                      ))}
                    </div>
                  </div>
                </div>
              ) : (
                <div className="cycles-clean-state">
                  <CheckCircle2 size={24} className="clean-icon" />
                  <div>
                    <strong>No Circular Dependencies Detected</strong>
                    <p>The analyzed dependency graph is a clean Directed Acyclic Graph (DAG).</p>
                  </div>
                </div>
              )}
            </div>

            {/* Dependency Graph with Cycle Highlighting */}
            <div className="card-panel">
              <InteractiveGraph
                data={graphData}
                highlightCyclePaths={cycles?.cycles || []}
                title="Dependency Graph (Cycles highlighted in red)"
              />
            </div>

            {/* Coupling Diagnostic Table */}
            <div className="card-panel">
              <div className="panel-header">
                <div>
                  <h3 className="panel-title">Coupling Breakdown & Explanations</h3>
                  <p className="panel-desc">
                    Afferent coupling ($C_a$), Efferent coupling ($C_e$), and Martin's Instability ($I = C_e / (C_a + C_e)$)
                  </p>
                </div>

                <div className="table-controls">
                  <div className="search-input-wrap">
                    <Search size={14} />
                    <input
                      type="text"
                      placeholder="Filter components..."
                      value={search}
                      onChange={(e) => setSearch(e.target.value)}
                    />
                  </div>

                  <select
                    value={filterLevel}
                    onChange={(e) => setFilterLevel(e.target.value)}
                    className="select-filter"
                  >
                    <option value="all">All Coupling Levels</option>
                    <option value="HIGH">High Coupling</option>
                    <option value="MEDIUM">Medium Coupling</option>
                    <option value="LOW">Low Coupling</option>
                  </select>
                </div>
              </div>

              <div className="table-responsive">
                <table className="devtwin-table">
                  <thead>
                    <tr>
                      <th>Component</th>
                      <th>Coupling Level</th>
                      <th>Incoming ($C_a$)</th>
                      <th>Outgoing ($C_e$)</th>
                      <th>Total</th>
                      <th>Instability ($I$)</th>
                      <th>Architectural Rationale</th>
                    </tr>
                  </thead>
                  <tbody>
                    {filteredCoupling.length === 0 ? (
                      <tr>
                        <td colSpan={7} className="table-empty">
                          No matching components found.
                        </td>
                      </tr>
                    ) : (
                      filteredCoupling.map((c) => (
                        <tr key={c.component}>
                          <td>
                            <strong>{c.name}</strong>
                            <small className="table-cell-sub">{c.component}</small>
                          </td>
                          <td>
                            <span className={`pill-badge badge-${c.coupling_level.toLowerCase()}`}>
                              {c.coupling_level}
                            </span>
                          </td>
                          <td>
                            <span className="conn-stat">{c.incoming}</span>
                          </td>
                          <td>
                            <span className="conn-stat">{c.outgoing}</span>
                          </td>
                          <td>
                            <strong>{c.total}</strong>
                          </td>
                          <td>
                            <span className="instability-value">{c.instability}</span>
                          </td>
                          <td>
                            <span className="table-explanation">{c.explanation}</span>
                          </td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              </div>
            </div>
          </>
        )}
      </div>
    </Layout>
  );
}
