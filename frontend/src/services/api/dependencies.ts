import { apiClient } from "./client";
import { DependencyMetrics, CyclesData, GraphData } from "./types";

export const dependenciesApi = {
  getOverview: async (projectId: string) => {
    const res = await apiClient.get(`/projects/${projectId}/dependencies`);
    return res.data;
  },

  getGraph: async (projectId: string): Promise<GraphData> => {
    const res = await apiClient.get<GraphData>(`/projects/${projectId}/dependencies/graph`);
    return res.data;
  },

  getMetrics: async (projectId: string): Promise<DependencyMetrics> => {
    const res = await apiClient.get<DependencyMetrics>(`/projects/${projectId}/dependencies/metrics`);
    return res.data;
  },

  getCycles: async (projectId: string): Promise<CyclesData> => {
    const res = await apiClient.get<CyclesData>(`/projects/${projectId}/dependencies/cycles`);
    return res.data;
  },
};
