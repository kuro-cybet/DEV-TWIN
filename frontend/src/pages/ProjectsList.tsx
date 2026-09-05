import React, { useState, useEffect, FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import {
  FolderPlus,
  Upload,
  GitBranch,
  Trash2,
  Play,
  CheckCircle2,
  Clock,
  Layers,
  ArrowRight,
  Sparkles,
  Info,
} from "lucide-react";
import { Layout } from "../components/Layout";
import { projectsApi } from "../services/api/projects";
import { Project } from "../services/api/types";
import { EmptyState, ErrorBanner } from "../components/EmptyState";

export function ProjectsList() {
  const navigate = useNavigate();
  const [projects, setProjects] = useState<Project[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Ingestion form state
  const [tab, setTab] = useState<"zip" | "github">("zip");
  const [name, setName] = useState("");
  const [repoUrl, setRepoUrl] = useState("");
  const [zipFile, setZipFile] = useState<File | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [submitError, setSubmitError] = useState<string | null>(null);

  const loadProjects = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await projectsApi.list();
      setProjects(data);
    } catch (err: any) {
      setError(err.message || "Failed to load projects.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadProjects();
  }, []);

  const handleCreate = async (e: FormEvent) => {
    e.preventDefault();
    setSubmitError(null);

    if (tab === "zip") {
      if (!zipFile) {
        setSubmitError("Please select a .zip project file to upload.");
        return;
      }
      setSubmitting(true);
      try {
        const newProj = await projectsApi.uploadZip(zipFile, name.trim() || undefined);
        setName("");
        setZipFile(null);
        await loadProjects();
        navigate(`/projects/${newProj.id}`);
      } catch (err: any) {
        setSubmitError(err.message || "Failed to upload project ZIP.");
      } finally {
        setSubmitting(false);
      }
    } else {
      if (!name.trim()) {
        setSubmitError("Please enter a project name.");
        return;
      }
      if (!repoUrl.trim()) {
        setSubmitError("Please enter a valid GitHub repository URL.");
        return;
      }
      setSubmitting(true);
      try {
        const newProj = await projectsApi.create({
          name: name.trim(),
          repository_url: repoUrl.trim(),
        });
        setName("");
        setRepoUrl("");
        await loadProjects();
        navigate(`/projects/${newProj.id}`);
      } catch (err: any) {
        setSubmitError(err.message || "Failed to create GitHub project.");
      } finally {
        setSubmitting(false);
      }
    }
  };

  const handleDelete = async (id: string, e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    if (!confirm("Are you sure you want to delete this project?")) return;
    try {
      await projectsApi.delete(id);
      setProjects((prev) => prev.filter((p) => p.id !== id));
    } catch (err: any) {
      alert(`Delete failed: ${err.message}`);
    }
  };

  return (
    <Layout>
      <div className="projects-page-wrap">
        {/* Hero Section */}
        <section className="hero-banner">
          <div className="hero-content">
            <span className="hero-eyebrow">INTELLIGENT SOFTWARE ARCHITECTURE TWIN</span>
            <h1 className="hero-title">DevTwin Workspace</h1>
            <p className="hero-subtitle">
              Automated architecture discovery, multi-level dependency intelligence, and change
              impact blast radius analysis for Python software systems.
            </p>
          </div>
        </section>

        {error && <ErrorBanner message={error} onRetry={loadProjects} />}

        {/* Project Ingestion Box */}
        <section className="ingestion-card">
          <div className="ingestion-tabs">
            <button
              className={`ingestion-tab ${tab === "zip" ? "active" : ""}`}
              onClick={() => {
                setTab("zip");
                setSubmitError(null);
              }}
            >
              <Upload size={16} /> Upload Project ZIP (Recommended)
            </button>
            <button
              className={`ingestion-tab ${tab === "github" ? "active" : ""}`}
              onClick={() => {
                setTab("github");
                setSubmitError(null);
              }}
            >
              <GitBranch size={16} /> GitHub Repository
            </button>
          </div>

          {tab === "github" && (
            <div className="ingestion-tip-box">
              <Info size={16} />
              <span>
                Note: For zero-latency ingestion without remote GitHub connection timeouts or rate-limits, you can download your repository as a ZIP and use the <strong>Upload Project ZIP</strong> tab.
              </span>
            </div>
          )}

          <form onSubmit={handleCreate} className="ingestion-form">
            {submitError && <div className="form-error-msg">{submitError}</div>}

            <div className="form-fields-row">
              <div className="form-group">
                <label className="form-label">Project Name</label>
                <input
                  type="text"
                  placeholder="e.g. My Fastapi Microservice"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  className="form-input"
                />
              </div>

              {tab === "zip" ? (
                <div className="form-group">
                  <label className="form-label">Source Code ZIP Archive (.zip)</label>
                  <input
                    type="file"
                    accept=".zip"
                    onChange={(e) => setZipFile(e.target.files?.[0] || null)}
                    className="form-file-input"
                  />
                </div>
              ) : (
                <div className="form-group">
                  <label className="form-label">GitHub Repository URL</label>
                  <input
                    type="url"
                    placeholder="https://github.com/owner/repository"
                    value={repoUrl}
                    onChange={(e) => setRepoUrl(e.target.value)}
                    className="form-input"
                  />
                </div>
              )}

              <div className="form-submit-group">
                <button type="submit" disabled={submitting} className="btn-primary btn-submit">
                  {submitting ? (
                    "Ingesting..."
                  ) : (
                    <>
                      <FolderPlus size={16} /> Ingest Project
                    </>
                  )}
                </button>
              </div>
            </div>
          </form>
        </section>

        {/* Projects Grid */}
        <section className="projects-grid-section">
          <div className="section-header">
            <h2>Your Projects ({projects.length})</h2>
          </div>

          {loading ? (
            <div className="loading-grid">
              <div className="skeleton-card" />
              <div className="skeleton-card" />
              <div className="skeleton-card" />
            </div>
          ) : projects.length === 0 ? (
            <EmptyState
              title="No Projects Ingested Yet"
              description="Upload a Python source code ZIP or enter a GitHub repository above to discover and analyze its software architecture."
              icon={<Layers size={40} />}
            />
          ) : (
            <div className="projects-grid">
              {projects.map((project) => (
                <Link
                  to={`/projects/${project.id}`}
                  key={project.id}
                  className="project-card-link"
                >
                  <div className="project-card">
                    <div className="project-card-top">
                      <h3 className="project-name">{project.name}</h3>
                      <button
                        onClick={(e) => handleDelete(project.id, e)}
                        title="Delete project"
                        className="btn-delete"
                      >
                        <Trash2 size={14} />
                      </button>
                    </div>

                    <p className="project-repo-url">
                      {project.repository_url || project.source_path || "Uploaded source archive"}
                    </p>

                    <div className="project-card-stats">
                      <div className="card-stat">
                        <span className="stat-label">Components</span>
                        <span className="stat-num">{project.components_count || 0}</span>
                      </div>
                      <div className="card-stat">
                        <span className="stat-label">Dependencies</span>
                        <span className="stat-num">{project.dependencies_count || 0}</span>
                      </div>
                    </div>

                    <div className="project-card-footer">
                      <span className={`status-pill pill-${project.status}`}>
                        {project.status === "analyzed" ? (
                          <>
                            <CheckCircle2 size={12} /> Analyzed
                          </>
                        ) : (
                          <>
                            <Clock size={12} /> {project.status}
                          </>
                        )}
                      </span>
                      <span className="open-link">
                        Open Workspace <ArrowRight size={13} />
                      </span>
                    </div>
                  </div>
                </Link>
              ))}
            </div>
          )}
        </section>
      </div>
    </Layout>
  );
}
