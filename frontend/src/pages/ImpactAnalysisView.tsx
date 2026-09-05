import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import {
  Zap,
  Layers,
  ArrowRight,
  AlertCircle,
  CheckCircle2,
  Info,
  Sliders,
  Sparkles,
} from "lucide-react";
import { Layout } from "../components/Layout";
import { InteractiveGraph } from "../components/InteractiveGraph";
import { EmptyState, ErrorBanner, LoadingSkeleton } from "../components/EmptyState";
import { projectsApi } from "../services/api/projects";
import { architectureApi } from "../services/api/architecture";
import { impactApi } from "../services/api/impact";
import { Project, ComponentNode, ImpactResult, GraphData } from "../services/api/types";

export function ImpactAnalysisView() {
  const { projectId = "" } = useParams();
  const [project, setProject] = useState<Project | null>(null);
  const [components, setComponents] = useState<ComponentNode[]>([]);
  const [selectedComponentId, setSelectedComponentId] = useState<string>("");
  const [impactResult, setImpactResult] = useState<ImpactResult | null>(null);
  const [analyzingImpact, setAnalyzingImpact] = useState(false);
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
        const comps = await architectureApi.getComponents(projectId);
        const list = comps.components || [];
        setComponents(list);
        if (list.length > 0 && !selectedComponentId) {
          setSelectedComponentId(list[0].id);
          // Automatically run initial impact analysis on first component
          runImpact(list[0].id);
        }
      }
    } catch (err: any) {
      setError(err.message || "Failed to load components for impact analysis.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [projectId]);

  const runImpact = async (compId?: string) => {
    const targetId = compId || selectedComponentId;
    if (!targetId || !projectId) return;
    setAnalyzingImpact(true);
    try {
      const res = await impactApi.getImpact(projectId, targetId);
      setImpactResult(res);
    } catch (err: any) {
      setError(err.message || "Failed to calculate change impact.");
    } finally {
      setAnalyzingImpact(false);
    }
  };

  return (
    <Layout activeProject={project} onRefreshProject={loadData}>
      <div className="impact-page-wrap">
        <div className="view-page-header">
          <div>
            <span className="view-eyebrow">MODULE 2 — CHANGE IMPACT SIMULATION</span>
            <h1 className="view-title">Potential Change Impact</h1>
            <p className="view-subtitle">
              Calculates the downstream ripple effect and blast radius when modifying a specific component.
            </p>
          </div>
        </div>

        {error && <ErrorBanner message={error} onRetry={loadData} />}

        {loading ? (
          <LoadingSkeleton />
        ) : !project || project.status !== "analyzed" ? (
          <EmptyState
            title="Analysis Required"
            description="Run project analysis first to calculate change impact reachability graphs."
            actionLabel="Run Analysis"
            onAction={async () => {
              await projectsApi.analyze(projectId);
              await loadData();
            }}
          />
        ) : (
          <>
            {/* Component Selector Control */}
            <div className="card-panel impact-control-panel">
              <div className="panel-header">
                <div>
                  <h3 className="panel-title">Select Target Component to Modify</h3>
                  <p className="panel-desc">Choose any file or module to simulate potential change propagation</p>
                </div>
              </div>

              <div className="impact-selector-row">
                <div className="selector-group">
                  <select
                    value={selectedComponentId}
                    onChange={(e) => {
                      setSelectedComponentId(e.target.value);
                      runImpact(e.target.value);
                    }}
                    className="impact-select"
                  >
                    {components.map((c) => (
                      <option key={c.id} value={c.id}>
                        [{c.type.toUpperCase()}] {c.name} ({c.file_path})
                      </option>
                    ))}
                  </select>
                </div>

                <button
                  onClick={() => runImpact()}
                  disabled={analyzingImpact}
                  className="btn-primary"
                >
                  <Zap size={15} /> {analyzingImpact ? "Calculating..." : "Analyze Impact"}
                </button>
              </div>
            </div>

            {/* Impact Results Summary */}
            {impactResult && (
              <>
                {/* Blast Radius Summary Cards */}
                <div className="metrics-grid">
                  <div className="metric-card blast-radius-card">
                    <span className="metric-label">Total Blast Radius</span>
                    <div className="metric-value-row">
                      <span className="metric-value">{impactResult.total_affected}</span>
                      <span className="metric-badge badge-warning">
                        {Math.round(
                          (impactResult.total_affected / Math.max(1, components.length)) * 100
                        )}
                        % of system
                      </span>
                    </div>
                    <span className="metric-subtext">Components requiring re-testing</span>
                  </div>

                  <div className="metric-card">
                    <span className="metric-label">Direct Impact (Depth 1)</span>
                    <div className="metric-value-row">
                      <span className="metric-value text-orange">{impactResult.direct_count}</span>
                    </div>
                    <span className="metric-subtext">Direct callers & importers</span>
                  </div>

                  <div className="metric-card">
                    <span className="metric-label">Indirect Impact (Depth 2+)</span>
                    <div className="metric-value-row">
                      <span className="metric-value text-indigo">{impactResult.indirect_count}</span>
                    </div>
                    <span className="metric-subtext">Transitive downstream dependents</span>
                  </div>

                  <div className="metric-card">
                    <span className="metric-label">Selected Target</span>
                    <div className="metric-value-row">
                      <span className="metric-value-sm" title={impactResult.selected_component}>
                        {impactResult.selected_component_name}
                      </span>
                    </div>
                    <span className="metric-subtext">{impactResult.selected_component}</span>
                  </div>
                </div>

                {/* Important Disclaimer Alert */}
                <div className="impact-disclaimer-banner">
                  <Info size={18} />
                  <span>{impactResult.disclaimer}</span>
                </div>

                {/* Impact Subgraph Visualization */}
                <div className="card-panel">
                  <div className="panel-header">
                    <div>
                      <h3 className="panel-title">Impact Propagation Subgraph</h3>
                      <p className="panel-desc">
                        Highlighted reachability: Selected node (Target), Direct dependents (Orange), Transitive dependents (Indigo)
                      </p>
                    </div>
                  </div>
                  <InteractiveGraph
                    data={impactResult.impact_graph}
                    selectedNodeId={impactResult.selected_component}
                    highlightDirectIds={impactResult.direct_impact}
                    highlightIndirectIds={impactResult.indirect_impact}
                    title="Change Impact Reachability Subgraph"
                  />
                </div>

                {/* Direct vs Indirect Impact Breakdown Tables */}
                <div className="two-col-grid">
                  {/* Direct Impact */}
                  <div className="card-panel">
                    <div className="panel-header">
                      <div>
                        <h4 className="panel-title text-orange">
                          Directly Affected Components ({impactResult.direct_count})
                        </h4>
                        <p className="panel-desc">Components directly importing or invoking this target</p>
                      </div>
                    </div>

                    <div className="impact-list">
                      {impactResult.direct_impact_nodes.length === 0 ? (
                        <div className="table-empty">No direct dependents found.</div>
                      ) : (
                        impactResult.direct_impact_nodes.map((node) => (
                          <div key={node.id} className="impact-item-row impact-row-direct">
                            <div className="impact-item-info">
                              <span className="pill-badge badge-file">{node.type}</span>
                              <strong>{node.name}</strong>
                              <small>{node.file_path}</small>
                            </div>
                            <span className="depth-badge depth-1">Direct (Depth 1)</span>
                          </div>
                        ))
                      )}
                    </div>
                  </div>

                  {/* Indirect Impact */}
                  <div className="card-panel">
                    <div className="panel-header">
                      <div>
                        <h4 className="panel-title text-indigo">
                          Indirectly Affected Components ({impactResult.indirect_count})
                        </h4>
                        <p className="panel-desc">Transitive dependencies downstream in the call graph</p>
                      </div>
                    </div>

                    <div className="impact-list">
                      {impactResult.indirect_impact_nodes.length === 0 ? (
                        <div className="table-empty">No transitive indirect dependents.</div>
                      ) : (
                        impactResult.indirect_impact_nodes.map((node) => (
                          <div key={node.id} className="impact-item-row impact-row-indirect">
                            <div className="impact-item-info">
                              <span className="pill-badge badge-file">{node.type}</span>
                              <strong>{node.name}</strong>
                              <small>{node.file_path}</small>
                            </div>
                            <span className="depth-badge depth-2">Transitive (Depth {node.depth})</span>
                          </div>
                        ))
                      )}
                    </div>
                  </div>
                </div>
              </>
            )}
          </>
        )}
      </div>
    </Layout>
  );
}
