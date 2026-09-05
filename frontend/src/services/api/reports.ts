import { apiClient } from "./client";
import { HealthSummaryReport, ArchitectureReport, DependencyReport } from "./types";

export const reportsApi = {
  getSummary: async (projectId: string): Promise<HealthSummaryReport> => {
    const res = await apiClient.get<HealthSummaryReport>(`/projects/${projectId}/reports/summary`);
    return res.data;
  },

  getArchitecture: async (projectId: string): Promise<ArchitectureReport> => {
    const res = await apiClient.get<ArchitectureReport>(`/projects/${projectId}/reports/architecture`);
    return res.data;
  },

  getDependencies: async (projectId: string): Promise<DependencyReport> => {
    const res = await apiClient.get<DependencyReport>(`/projects/${projectId}/reports/dependencies`);
    return res.data;
  },
};
