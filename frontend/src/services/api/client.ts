import axios from "axios";

export const apiClient = axios.create({
  baseURL: (import.meta as any).env?.VITE_API_URL || "http://localhost:8000/api/v1",
  headers: {
    "Content-Type": "application/json",
  },
  timeout: 60000,
});

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    let message =
      error.response?.data?.detail ||
      error.message ||
      "An unexpected server communication error occurred.";

    if (error.code === "ECONNABORTED" || message.includes("timeout")) {
      message =
        "Network timeout exceeded while contacting the remote repository. Tip: Download your project as a ZIP archive and use the 'Upload Project ZIP' tab for instant analysis.";
    }

    return Promise.reject(new Error(message));
  }
);
