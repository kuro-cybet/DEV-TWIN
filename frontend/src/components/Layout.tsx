import React, { useEffect, useState } from "react";
import { Link, NavLink, useNavigate, useParams } from "react-router-dom";
import {
  LayoutDashboard,
  Layers,
  GitFork,
  Zap,
  FileText,
  FolderKanban,
  Play,
  RefreshCw,
  Code2,
  CheckCircle2,
  AlertTriangle,
  Clock,
} from "lucide-react";
import { projectsApi } from "../services/api/projects";
import { Project } from "../services/api/types";

interface LayoutProps {
  children: React.ReactNode;
  activeProject?: Project | null;
  onRefreshProject?: () => void;
}

export function Layout({ children, activeProject, onRefreshProject }: LayoutProps) {
  const { projectId } = useParams();
  const navigate = useNavigate();
  const [allProjects, setAllProjects] = useState<Project[]>([]);
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [analyzeMsg, setAnalyzeMsg] = useState<string | null>(null);

  useEffect(() => {
    projectsApi.list().then(setAllProjects).catch(() => {});
  }, [projectId]);

  const handleRunAnalysis = async () => {
    if (!projectId) return;
    setIsAnalyzing(true);
    setAnalyzeMsg(null);
    try {
      await projectsApi.analyze(projectId);
      setAnalyzeMsg("Analysis complete!");
      onRefreshProject?.();
      setTimeout(() => setAnalyzeMsg(null), 3000);
    } catch (err: any) {
      setAnalyzeMsg(`Failed: ${err.message}`);
      setTimeout(() => setAnalyzeMsg(null), 4000);
    } finally {
      setIsAnalyzing(false);
    }
  };

  const getStatusBadge = (status?: string) => {
    switch (status) {
      case "analyzed":
        return (
          <span className="status-badge status-analyzed">
            <CheckCircle2 size={12} /> Analyzed
          </span>
        );
      case "uploaded":
        return (
          <span className="status-badge status-uploaded">
            <Clock size={12} /> Ready to Analyze
          </span>
        );
      case "awaiting_source":
        return (
          <span className="status-badge status-warning">
            <AlertTriangle size={12} /> Awaiting Source
          </span>
        );
      default:
        return <span className="status-badge status-neutral">{status || "Created"}</span>;
    }
  };

  return (
    <div className="devtwin-app-shell">
      {/* Top Header */}
      <header className="devtwin-header">
        <div className="header-left">
          <Link to="/" className="brand-logo">
            <div className="brand-icon">
              <Code2 size={20} />
            </div>
            <div className="brand-text">
              <span className="brand-name">DevTwin</span>
              <span className="brand-tagline">Architecture & Dependency Intelligence</span>
            </div>
          </Link>

          {allProjects.length > 0 && projectId && (
            <div className="project-switcher-wrap">
              <span className="switcher-label">Project:</span>
              <select
                value={projectId}
                onChange={(e) => navigate(`/projects/${e.target.value}`)}
                className="project-select"
              >
                {allProjects.map((p) => (
                  <option key={p.id} value={p.id}>
                    {p.name}
                  </option>
                ))}
              </select>
            </div>
          )}
        </div>

        <div className="header-right">
          {activeProject && (
            <>
              {getStatusBadge(activeProject.status)}
              <button
                onClick={handleRunAnalysis}
                disabled={isAnalyzing}
                className="btn-primary btn-run-analysis"
              >
                {isAnalyzing ? (
                  <>
                    <RefreshCw size={14} className="spin" /> Analyzing...
                  </>
                ) : (
                  <>
                    <Play size={14} /> Run Analysis
                  </>
                )}
              </button>
            </>
          )}

          {analyzeMsg && <span className="analysis-toast">{analyzeMsg}</span>}
        </div>
      </header>

      <div className="devtwin-body">
        {/* Left Sidebar Navigation */}
        {projectId ? (
          <aside className="devtwin-sidebar">
            <div className="sidebar-section-title">PROJECT VIEWS</div>
            <nav className="sidebar-nav">
              <NavLink
                to={`/projects/${projectId}`}
                end
                className={({ isActive }) => `sidebar-link ${isActive ? "active" : ""}`}
              >
                <LayoutDashboard size={18} />
                <span>Overview</span>
              </NavLink>

              <NavLink
                to={`/projects/${projectId}/architecture`}
                className={({ isActive }) => `sidebar-link ${isActive ? "active" : ""}`}
              >
                <Layers size={18} />
                <span>Architecture</span>
              </NavLink>

              <NavLink
                to={`/projects/${projectId}/dependencies`}
                className={({ isActive }) => `sidebar-link ${isActive ? "active" : ""}`}
              >
                <GitFork size={18} />
                <span>Dependencies</span>
              </NavLink>

              <NavLink
                to={`/projects/${projectId}/impact`}
                className={({ isActive }) => `sidebar-link ${isActive ? "active" : ""}`}
              >
                <Zap size={18} />
                <span>Change Impact</span>
              </NavLink>

              <NavLink
                to={`/projects/${projectId}/reports`}
                className={({ isActive }) => `sidebar-link ${isActive ? "active" : ""}`}
              >
                <FileText size={18} />
                <span>Reports</span>
              </NavLink>
            </nav>

            <div className="sidebar-divider" />

            <div className="sidebar-section-title">MANAGEMENT</div>
            <nav className="sidebar-nav">
              <NavLink
                to="/projects"
                className={({ isActive }) => `sidebar-link ${isActive ? "active" : ""}`}
              >
                <FolderKanban size={18} />
                <span>All Projects</span>
              </NavLink>
            </nav>
          </aside>
        ) : null}

        {/* Main Content Viewport */}
        <main className={`devtwin-main-content ${!projectId ? "full-width" : ""}`}>
          {children}
        </main>
      </div>
    </div>
  );
}
