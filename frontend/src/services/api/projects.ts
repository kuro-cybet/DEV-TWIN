import { apiClient } from "./client";
import { Project } from "./types";

export const projectsApi = {
  list: async (): Promise<Project[]> => {
    const res = await apiClient.get<Project[]>("/projects");
    return res.data;
  },

  get: async (id: string): Promise<Project> => {
    const res = await apiClient.get<Project>(`/projects/${id}`);
    return res.data;
  },

  create: async (payload: { name: string; repository_url?: string | null; source_path?: string | null }): Promise<Project> => {
    const res = await apiClient.post<Project>("/projects", payload);
    return res.data;
  },

  uploadZip: async (file: File, name?: string): Promise<Project> => {
    const formData = new FormData();
    formData.append("file", file);
    if (name) formData.append("name", name);
    const res = await apiClient.post<Project>("/projects/upload", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    return res.data;
  },

  delete: async (id: string): Promise<{ message: string; id: string }> => {
    const res = await apiClient.delete<{ message: string; id: string }>(`/projects/${id}`);
    return res.data;
  },

  analyze: async (id: string): Promise<{ project_id: string; status: string; components: number; dependencies: number; message?: string }> => {
    const res = await apiClient.post(`/projects/${id}/analyze`);
    return res.data;
  },
};
