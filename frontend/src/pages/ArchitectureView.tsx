import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { Layers, FileCode, Box, Code, Search, Filter } from "lucide-react";
import { Layout } from "../components/Layout";
import { InteractiveGraph } from "../components/InteractiveGraph";
import { EmptyState, ErrorBanner, LoadingSkeleton } from "../components/EmptyState";
import { projectsApi } from "../services/api/projects";
import { architectureApi } from "../services/api/architecture";
import { Project, GraphData, ComponentNode } from "../services/api/types";

export function ArchitectureView() {
  const { projectId = "" } = useParams();
  const [project, setProject] = useState<Project | null>(null);
  const [graphData, setGraphData] = useState<GraphData | null>(null);
  const [level, setLevel] = useState<"file" | "component">("file");
  const [componentsList, setComponentsList] = useState<ComponentNode[]>([]);
  const [selectedNode, setSelectedNode] = useState<ComponentNode | null>(null);
  const [tableSearch, setTableSearch] = useState("");
  const [typeFilter, setTypeFilter] = useState("all");
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
        const [g, comps] = await Promise.all([
          architectureApi.getGraph(projectId, level),
          architectureApi.getComponents(projectId),
        ]);
        setGraphData(g);
        setComponentsList(comps.components || []);
      }
    } catch (err: any) {
      setError(err.message || "Failed to load architecture data.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [projectId, level]);

  const filteredComponents = componentsList.filter((c) => {
    const matchesSearch =
      tableSearch.trim() === "" ||
      c.name.toLowerCase().includes(tableSearch.toLowerCase()) ||
      c.file_path.toLowerCase().includes(tableSearch.toLowerCase());
    const matchesType = typeFilter === "all" || c.type === typeFilter;
    return matchesSearch && matchesType;
  });

  const filesCount = componentsList.filter((c) => c.type === "file").length;
  const classesCount = componentsList.filter((c) => c.type === "class").length;
  const functionsCount = componentsList.filter((c) => c.type === "function" || c.type === "method").length;

  return (
    <Layout activeProject={project} onRefreshProject={loadData}>
      <div className="architecture-page-wrap">
        <div className="view-page-header">
          <div>
            <span className="view-eyebrow">MODULE 1 — ARCHITECTURE DISCOVERY</span>
            <h1 className="view-title">System Architecture</h1>
            <p className="view-subtitle">
              Visual map of files, classes, functions, and internal relationships.
            </p>
          </div>

          {/* Level of Detail Switcher */}
          <div className="level-toggle-group">
            <button
              className={`toggle-btn ${level === "file" ? "active" : ""}`}
              onClick={() => setLevel("file")}
            >
              <FileCode size={14} /> File Level
            </button>
            <button
              className={`toggle-btn ${level === "component" ? "active" : ""}`}
              onClick={() => setLevel("component")}
            >
              <Box size={14} /> Component Level
            </button>
          </div>
        </div>

        {error && <ErrorBanner message={error} onRetry={loadData} />}

        {loading ? (
          <LoadingSkeleton />
        ) : !project || project.status !== "analyzed" ? (
          <EmptyState
            title="Analysis Required"
            description="Run an architecture analysis on this project to view its component graph."
            actionLabel="Run Analysis"
            onAction={async () => {
              await projectsApi.analyze(projectId);
              await loadData();
            }}
          />
        ) : (
          <>
            {/* Quick stats banner */}
            <div className="arch-stats-row">
              <div className="arch-stat-box">
                <span className="arch-stat-label">Files</span>
                <span className="arch-stat-num">{filesCount}</span>
              </div>
              <div className="arch-stat-box">
                <span className="arch-stat-label">Classes</span>
                <span className="arch-stat-num">{classesCount}</span>
              </div>
              <div className="arch-stat-box">
                <span className="arch-stat-label">Functions & Methods</span>
                <span className="arch-stat-num">{functionsCount}</span>
              </div>
              <div className="arch-stat-box">
                <span className="arch-stat-label">Relationships</span>
                <span className="arch-stat-num">{graphData?.edges.length || 0}</span>
              </div>
            </div>

            {/* Interactive Graph */}
            <div className="card-panel">
              <InteractiveGraph
                data={graphData}
                selectedNodeId={selectedNode?.id}
                onSelectNode={(n) => setSelectedNode(n)}
                title={`Architecture Graph (${level === "file" ? "File View" : "Detailed Component View"})`}
              />
            </div>

            {/* Component Inventory Table */}
            <div className="card-panel">
              <div className="panel-header">
                <div>
                  <h3 className="panel-title">Component Directory</h3>
                  <p className="panel-desc">All detected Python files, classes, and callable functions</p>
                </div>

                <div className="table-controls">
                  <div className="search-input-wrap">
                    <Search size={14} />
                    <input
                      type="text"
                      placeholder="Filter components..."
                      value={tableSearch}
                      onChange={(e) => setTableSearch(e.target.value)}
                    />
                  </div>

                  <select
                    value={typeFilter}
                    onChange={(e) => setTypeFilter(e.target.value)}
                    className="select-filter"
                  >
                    <option value="all">All Types</option>
                    <option value="file">Files</option>
                    <option value="class">Classes</option>
                    <option value="function">Functions</option>
                    <option value="method">Methods</option>
                  </select>
                </div>
              </div>

              <div className="table-responsive">
                <table className="devtwin-table">
                  <thead>
                    <tr>
                      <th>Type</th>
                      <th>Name</th>
                      <th>File Location</th>
                      <th>Line</th>
                      <th>Parent</th>
                    </tr>
                  </thead>
                  <tbody>
                    {filteredComponents.length === 0 ? (
                      <tr>
                        <td colSpan={5} className="table-empty">
                          No matching components found.
                        </td>
                      </tr>
                    ) : (
                      filteredComponents.map((comp) => (
                        <tr
                          key={comp.id}
                          className={selectedNode?.id === comp.id ? "row-selected" : ""}
                          onClick={() => setSelectedNode(comp)}
                          style={{ cursor: "pointer" }}
                        >
                          <td>
                            <span className={`pill-badge badge-${comp.type}`}>{comp.type}</span>
                          </td>
                          <td>
                            <strong>{comp.name}</strong>
                          </td>
                          <td>
                            <span className="code-font">{comp.file_path}</span>
                          </td>
                          <td>{comp.line_number ? `L${comp.line_number}` : "—"}</td>
                          <td>
                            <span className="text-muted">{comp.parent || "root"}</span>
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
