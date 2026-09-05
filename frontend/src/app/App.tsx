import React from "react";
import { Link, Route, Routes, Navigate } from "react-router-dom";
import { ProjectsList } from "../pages/ProjectsList";
import { ProjectOverview } from "../pages/ProjectOverview";
import { ArchitectureView } from "../pages/ArchitectureView";
import { DependenciesView } from "../pages/DependenciesView";
import { ImpactAnalysisView } from "../pages/ImpactAnalysisView";
import { ReportsView } from "../pages/ReportsView";

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<ProjectsList />} />
      <Route path="/projects" element={<ProjectsList />} />
      <Route path="/projects/:projectId" element={<ProjectOverview />} />
      <Route path="/projects/:projectId/architecture" element={<ArchitectureView />} />
      <Route path="/projects/:projectId/dependencies" element={<DependenciesView />} />
      <Route path="/projects/:projectId/impact" element={<ImpactAnalysisView />} />
      <Route path="/projects/:projectId/reports" element={<ReportsView />} />
      <Route
        path="*"
        element={
          <div className="not-found-page">
            <h1>404</h1>
            <p>Page Not Found</p>
            <Link to="/" className="btn-primary">
              Return to DevTwin Workspace
            </Link>
          </div>
        }
      />
    </Routes>
  );
}
