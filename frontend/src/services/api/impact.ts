import { apiClient } from "./client";
import { ImpactResult } from "./types";

export const impactApi = {
  getImpact: async (projectId: string, componentId: string): Promise<ImpactResult> => {
    const encoded = encodeURIComponent(componentId);
    const res = await apiClient.get<ImpactResult>(`/projects/${projectId}/impact/${encoded}`);
    return res.data;
  },

  analyzeImpact: async (projectId: string, componentId: string): Promise<ImpactResult> => {
    const res = await apiClient.post<ImpactResult>(`/projects/${projectId}/impact/analyze`, {
      component_id: componentId,
    });
    return res.data;
  },
};
