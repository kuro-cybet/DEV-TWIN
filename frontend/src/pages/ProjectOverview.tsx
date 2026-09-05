import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import {
  Layers,
  GitFork,
  AlertTriangle,
  Activity,
  ShieldCheck,
  Zap,
  ArrowUpRight,
  RefreshCw,
  FileCode,
  Box,
} from "lucide-react";
import { Layout } from "../components/Layout";
import { MetricCard } from "../components/MetricCard";
import { InteractiveGraph } from "../components/InteractiveGraph";
import { EmptyState, ErrorBanner, LoadingSkeleton } from "../components/EmptyState";
import { projectsApi } from "../services/api/projects";
import { dependenciesApi } from "../services/api/dependencies";
import { reportsApi } from "../services/api/reports";
import { architectureApi } from "../services/api/architecture";
import {
  Project,
  DependencyMetrics,
  CyclesData,
  HealthSummaryReport,
  GraphData,
} from "../services/api/types";

export function ProjectOverview() {
  const { projectId = "" } = useParams();
  const [project, setProject] = useState<Project | null>(null);
  const [metrics, setMetrics] = useState<DependencyMetrics | null>(null);
  const [cycles, setCycles] = useState<CyclesData | null>(null);
  const [report, setReport] = useState<HealthSummaryReport | null>(null);
  const [graphData, setGraphData] = useState<GraphData | null>(null);
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
        const [m, c, r, g] = await Promise.all([
          dependenciesApi.getMetrics(projectId),
          dependenciesApi.getCycles(projectId),
          reportsApi.getSummary(projectId),
          architectureApi.getGraph(projectId, "file"),
        ]);
        setMetrics(m);
        setCycles(c);
        setReport(r);
        setGraphData(g);
      }
    } catch (err: any) {
      setError(err.message || "Failed to load project details.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [projectId]);

  if (loading) {
    return (
      <Layout activeProject={project} onRefreshProject={loadData}>
        <LoadingSkeleton />
      </Layout>
    );
  }

  if (!project) {
    return (
      <Layout>
        <EmptyState
          title="Project Not Found"
          description="The requested project does not exist or has been deleted."
          actionLabel="Back to Projects"
          onAction={() => window.location.assign("/projects")}
        />
      </Layout>
    );
  }

  const isAnalyzed = project.status === "analyzed" && metrics;

  return (
    <Layout activeProject={project} onRefreshProject={loadData}>
      <div className="overview-page-wrap">
        {/* Page Header */}
        <div className="view-page-header">
          <div>
            <span className="view-eyebrow">PROJECT DASHBOARD</span>
            <h1 className="view-title">{project.name}</h1>
            <p className="view-subtitle">
              {project.repository_url
                ? `GitHub: ${project.repository_url}`
                : `Source: ${project.source_path || "Uploaded Archive"}`}
            </p>
          </div>
        </div>

        {error && <ErrorBanner message={error} onRetry={loadData} />}

        {!isAnalyzed ? (
          <EmptyState
            title="Analysis Required"
            description="This project has been ingested but source code analysis has not been executed yet. Click 'Run Analysis' to extract modules, classes, functions, and dependency graphs."
            actionLabel="Run Analysis Now"
            onAction={async () => {
              await projectsApi.analyze(projectId);
              await loadData();
            }}
            icon={<Activity size={42} />}
          />
        ) : (
          <>
            {/* Health Grade Banner */}
            {report && (
              <div className={`health-summary-banner grade-card-${report.health_grade.toLowerCase()}`}>
                <div className="health-grade-badge">
                  <span className="grade-letter">{report.health_grade}</span>
                  <span className="grade-score">{report.health_score}/100</span>
                </div>
                <div className="health-summary-text">
                  <h3>Architecture Health Score</h3>
                  <p>{report.summary}</p>
                </div>
              </div>
            )}

            {/* Key Metrics Cards Row */}
            <div className="metrics-grid">
              <MetricCard
                label="Total Components"
                value={metrics.components}
                subtext="Files, modules, and symbols"
                tooltip="The total count of normalized software components discovered in the repository."
                icon={<Layers size={20} />}
              />

              <MetricCard
                label="Total Dependencies"
                value={metrics.dependencies}
                subtext={`${metrics.avg_dependencies_per_component} avg per component`}
                tooltip="Directed import, inheritance, and call relationships established between components."
                icon={<GitFork size={20} />}
              />

              <MetricCard
                label="Circular Dependencies"
                value={cycles?.count ?? 0}
                subtext={cycles?.count === 0 ? "No cyclic coupling" : `${cycles?.count} cycle paths detected`}
                badge={{
                  text: cycles?.severity || "NONE",
                  variant:
                    cycles?.severity === "NONE"
                      ? "success"
                      : cycles?.severity === "LOW"
                      ? "warning"
                      : "danger",
                }}
                tooltip="Circular dependency chains (e.g. A → B → C → A) that create tight coupling."
                icon={<AlertTriangle size={20} />}
              />

              <MetricCard
                label="Graph Density"
                value={`${metrics.density_percentage}%`}
                subtext="System interconnectedness"
                tooltip="The ratio of actual dependencies to the maximum possible connections between components."
                icon={<Activity size={20} />}
              />
            </div>

            {/* Graph Preview Card */}
            <div className="card-panel">
              <div className="panel-header">
                <div>
                  <h2 className="panel-title">Interactive Architecture Preview</h2>
                  <p className="panel-desc">Multi-level component and dependency visualizer</p>
                </div>
                <Link to={`/projects/${projectId}/architecture`} className="panel-link">
                  Detailed Architecture <ArrowUpRight size={14} />
                </Link>
              </div>
              <InteractiveGraph
                data={graphData}
                highlightCyclePaths={cycles?.cycles || []}
                title="Architecture & Dependency Flow"
              />
            </div>

            {/* Two-Column Deep-Dive Sections */}
            <div className="two-col-grid">
              {/* Critical Architectural Hubs */}
              <div className="card-panel">
                <div className="panel-header">
                  <div>
                    <h3 className="panel-title">Critical Architectural Hubs</h3>
                    <p className="panel-desc">Components with high connectivity and routing betweenness</p>
                  </div>
                </div>

                <div className="table-responsive">
                  <table className="devtwin-table">
                    <thead>
                      <tr>
                        <th>Component</th>
                        <th>Level</th>
                        <th>Connections</th>
                        <th>Rationale</th>
                      </tr>
                    </thead>
                    <tbody>
                      {metrics.critical_components.slice(0, 5).map((c) => (
                        <tr key={c.component}>
                          <td>
                            <strong>{c.name}</strong>
                            <small className="table-cell-sub">{c.component}</small>
                          </td>
                          <td>
                            <span className={`pill-badge badge-${c.level.toLowerCase()}`}>
                              {c.level}
                            </span>
                          </td>
                          <td>
                            <span className="conn-stat">
                              {c.incoming} in · {c.outgoing} out
                            </span>
                          </td>
                          <td>
                            <span className="table-explanation">{c.explanation}</span>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>

              {/* High Coupling Components */}
              <div className="card-panel">
                <div className="panel-header">
                  <div>
                    <h3 className="panel-title">Highest Coupling Components</h3>
                    <p className="panel-desc">Components with the largest blast radius if changed</p>
                  </div>
                  <Link to={`/projects/${projectId}/dependencies`} className="panel-link">
                    Full Coupling View <ArrowUpRight size={14} />
                  </Link>
                </div>

                <div className="table-responsive">
                  <table className="devtwin-table">
                    <thead>
                      <tr>
                        <th>Component</th>
                        <th>Coupling</th>
                        <th>Afferent / Efferent</th>
                      </tr>
                    </thead>
                    <tbody>
                      {metrics.high_coupling.slice(0, 5).map((item) => (
                        <tr key={item.component}>
                          <td>
                            <strong>{item.name}</strong>
                            <small className="table-cell-sub">{item.component}</small>
                          </td>
                          <td>
                            <span className={`pill-badge badge-${item.coupling_level.toLowerCase()}`}>
                              {item.coupling_level} ({item.total})
                            </span>
                          </td>
                          <td>
                            <span className="conn-stat">
                              Ca: {item.incoming} | Ce: {item.outgoing}
                            </span>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>

            {/* Architectural Recommendations & Risks */}
            {report && report.top_risks.length > 0 && (
              <div className="card-panel">
                <div className="panel-header">
                  <h3 className="panel-title">Architectural Health & Risks</h3>
                </div>
                <div className="risks-list">
                  {report.top_risks.map((risk, idx) => (
                    <div key={idx} className={`risk-item risk-${risk.severity.toLowerCase()}`}>
                      <div className="risk-header">
                        <span className={`risk-pill pill-${risk.severity.toLowerCase()}`}>
                          {risk.severity} RISK
                        </span>
                        <strong>{risk.title}</strong>
                      </div>
                      <p className="risk-desc">{risk.description}</p>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </>
        )}
      </div>
    </Layout>
  );
}
