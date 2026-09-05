import { apiClient } from "./client";
import { GraphData, ComponentNode } from "./types";

export const architectureApi = {
  getOverview: async (projectId: string) => {
    const res = await apiClient.get(`/projects/${projectId}/architecture`);
    return res.data;
  },

  getComponents: async (projectId: string): Promise<{ components: ComponentNode[]; total: number; by_type: Record<string, number> }> => {
    const res = await apiClient.get(`/projects/${projectId}/architecture/components`);
    return res.data;
  },

  getGraph: async (projectId: string, level: "file" | "component" = "file"): Promise<GraphData> => {
    const res = await apiClient.get<GraphData>(`/projects/${projectId}/architecture/graph`, {
      params: { level },
    });
    return res.data;
  },
};
