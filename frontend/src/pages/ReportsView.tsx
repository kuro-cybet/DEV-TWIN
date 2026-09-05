import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import {
  FileText,
  ShieldCheck,
  Layers,
  GitFork,
  Copy,
  Check,
  Printer,
  Download,
  AlertTriangle,
} from "lucide-react";
import { Layout } from "../components/Layout";
import { EmptyState, ErrorBanner, LoadingSkeleton } from "../components/EmptyState";
import { projectsApi } from "../services/api/projects";
import { reportsApi } from "../services/api/reports";
import {
  Project,
  HealthSummaryReport,
  ArchitectureReport,
  DependencyReport,
} from "../services/api/types";

export function ReportsView() {
  const { projectId = "" } = useParams();
  const [project, setProject] = useState<Project | null>(null);
  const [activeTab, setActiveTab] = useState<"summary" | "architecture" | "dependencies">("summary");
  const [summaryReport, setSummaryReport] = useState<HealthSummaryReport | null>(null);
  const [archReport, setArchReport] = useState<ArchitectureReport | null>(null);
  const [depReport, setDepReport] = useState<DependencyReport | null>(null);
  const [copied, setCopied] = useState(false);
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
        const [s, a, d] = await Promise.all([
          reportsApi.getSummary(projectId),
          reportsApi.getArchitecture(projectId),
          reportsApi.getDependencies(projectId),
        ]);
        setSummaryReport(s);
        setArchReport(a);
        setDepReport(d);
      }
    } catch (err: any) {
      setError(err.message || "Failed to load reports.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [projectId]);

  const handleCopyJson = () => {
    let dataToCopy: any = summaryReport;
    if (activeTab === "architecture") dataToCopy = archReport;
    if (activeTab === "dependencies") dataToCopy = depReport;

    navigator.clipboard.writeText(JSON.stringify(dataToCopy, null, 2));
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const handlePrint = () => {
    window.print();
  };

  return (
    <Layout activeProject={project} onRefreshProject={loadData}>
      <div className="reports-page-wrap">
        <div className="view-page-header">
          <div>
            <span className="view-eyebrow">DIAGNOSTIC REPORTS</span>
            <h1 className="view-title">Architecture & Dependency Reports</h1>
            <p className="view-subtitle">
              Comprehensive architectural audits, health grading, and coupling diagnostics.
            </p>
          </div>

          <div className="report-action-buttons">
            <button onClick={handleCopyJson} className="btn-secondary">
              {copied ? <Check size={14} className="text-green" /> : <Copy size={14} />}
              {copied ? "Copied JSON" : "Copy JSON"}
            </button>
            <button onClick={handlePrint} className="btn-secondary">
              <Printer size={14} /> Print Report
            </button>
          </div>
        </div>

        {error && <ErrorBanner message={error} onRetry={loadData} />}

        {loading ? (
          <LoadingSkeleton />
        ) : !project || project.status !== "analyzed" ? (
          <EmptyState
            title="Analysis Required"
            description="Run analysis to generate comprehensive architectural health and dependency diagnostic reports."
            actionLabel="Run Analysis"
            onAction={async () => {
              await projectsApi.analyze(projectId);
              await loadData();
            }}
          />
        ) : (
          <>
            {/* Report Navigation Tabs */}
            <div className="report-tabs-nav">
              <button
                className={`report-tab-btn ${activeTab === "summary" ? "active" : ""}`}
                onClick={() => setActiveTab("summary")}
              >
                <ShieldCheck size={16} /> Health Summary
              </button>
              <button
                className={`report-tab-btn ${activeTab === "architecture" ? "active" : ""}`}
                onClick={() => setActiveTab("architecture")}
              >
                <Layers size={16} /> Architecture Inventory
              </button>
              <button
                className={`report-tab-btn ${activeTab === "dependencies" ? "active" : ""}`}
                onClick={() => setActiveTab("dependencies")}
              >
                <GitFork size={16} /> Dependency Diagnostic
              </button>
            </div>

            {/* TAB 1: Health Summary Report */}
            {activeTab === "summary" && summaryReport && (
              <div className="report-content-panel">
                <div className="report-hero-card">
                  <div className="report-grade-circle">
                    <span className="grade-val">{summaryReport.health_grade}</span>
                    <span className="score-val">{summaryReport.health_score}/100</span>
                  </div>
                  <div className="report-hero-meta">
                    <h2>{summaryReport.project_name} Health Audit</h2>
                    <p>{summaryReport.summary}</p>
                    <span className="report-timestamp">
                      Generated at {new Date(summaryReport.generated_at).toLocaleString()}
                    </span>
                  </div>
                </div>

                <div className="card-panel">
                  <h3 className="panel-title">Top Architectural Risks</h3>
                  <div className="risks-list">
                    {summaryReport.top_risks.map((risk, i) => (
                      <div key={i} className={`risk-item risk-${risk.severity.toLowerCase()}`}>
                        <div className="risk-header">
                          <span className={`risk-pill pill-${risk.severity.toLowerCase()}`}>
                            {risk.severity}
                          </span>
                          <strong>{risk.title}</strong>
                        </div>
                        <p className="risk-desc">{risk.description}</p>
                      </div>
                    ))}
                  </div>
                </div>

                <div className="card-panel">
                  <h3 className="panel-title">Actionable Recommendations</h3>
                  <ul className="recommendations-list">
                    {summaryReport.recommendations.map((rec, i) => (
                      <li key={i}>{rec}</li>
                    ))}
                  </ul>
                </div>
              </div>
            )}

            {/* TAB 2: Architecture Inventory Report */}
            {activeTab === "architecture" && archReport && (
              <div className="report-content-panel">
                <div className="metrics-grid">
                  <div className="metric-card">
                    <span className="metric-label">Total Files</span>
                    <span className="metric-value">{archReport.total_files}</span>
                  </div>
                  <div className="metric-card">
                    <span className="metric-label">Modules / Packages</span>
                    <span className="metric-value">{archReport.total_modules}</span>
                  </div>
                  <div className="metric-card">
                    <span className="metric-label">Classes</span>
                    <span className="metric-value">{archReport.total_classes}</span>
                  </div>
                  <div className="metric-card">
                    <span className="metric-label">Functions & Methods</span>
                    <span className="metric-value">{archReport.total_functions}</span>
                  </div>
                </div>

                <div className="card-panel">
                  <h3 className="panel-title">Module Distribution</h3>
                  <div className="table-responsive">
                    <table className="devtwin-table">
                      <thead>
                        <tr>
                          <th>Module Directory</th>
                          <th>Files Count</th>
                          <th>Contained Files</th>
                        </tr>
                      </thead>
                      <tbody>
                        {archReport.modules_breakdown.map((m, i) => (
                          <tr key={i}>
                            <td>
                              <strong>{m.module}</strong>
                            </td>
                            <td>{m.files_count}</td>
                            <td>
                              <span className="code-font">{m.files.join(", ")}</span>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </div>

                {archReport.inheritance_hierarchies.length > 0 && (
                  <div className="card-panel">
                    <h3 className="panel-title">Inheritance Hierarchies</h3>
                    <div className="table-responsive">
                      <table className="devtwin-table">
                        <thead>
                          <tr>
                            <th>Derived Class</th>
                            <th>Base Class</th>
                            <th>Confidence</th>
                          </tr>
                        </thead>
                        <tbody>
                          {archReport.inheritance_hierarchies.map((h, i) => (
                            <tr key={i}>
                              <td>
                                <strong>{h.derived}</strong>
                              </td>
                              <td>{h.base}</td>
                              <td>{Math.round(h.confidence * 100)}%</td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  </div>
                )}
              </div>
            )}

            {/* TAB 3: Dependency Diagnostic Report */}
            {activeTab === "dependencies" && depReport && (
              <div className="report-content-panel">
                <div className="metrics-grid">
                  <div className="metric-card">
                    <span className="metric-label">Total Dependencies</span>
                    <span className="metric-value">{depReport.total_dependencies}</span>
                  </div>
                  <div className="metric-card">
                    <span className="metric-label">Graph Density</span>
                    <span className="metric-value">{Math.round(depReport.graph_density * 100)}%</span>
                  </div>
                  <div className="metric-card">
                    <span className="metric-label">Circular Cycles</span>
                    <span className="metric-value">{depReport.cycles_count}</span>
                  </div>
                  <div className="metric-card">
                    <span className="metric-label">Avg Instability Index</span>
                    <span className="metric-value">{depReport.instability_index_avg}</span>
                  </div>
                </div>

                <div className="card-panel">
                  <h3 className="panel-title">Coupling & Instability Diagnostic</h3>
                  <div className="table-responsive">
                    <table className="devtwin-table">
                      <thead>
                        <tr>
                          <th>Component</th>
                          <th>Coupling Level</th>
                          <th>Afferent ($C_a$)</th>
                          <th>Efferent ($C_e$)</th>
                          <th>Instability ($I$)</th>
                          <th>Diagnostic</th>
                        </tr>
                      </thead>
                      <tbody>
                        {depReport.coupling_analysis.map((c) => (
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
                            <td>{c.incoming}</td>
                            <td>{c.outgoing}</td>
                            <td>{c.instability}</td>
                            <td>
                              <span className="table-explanation">{c.explanation}</span>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
            )}
          </>
        )}
      </div>
    </Layout>
  );
}
