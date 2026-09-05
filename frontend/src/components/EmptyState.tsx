import React from "react";
import { FolderPlus, RefreshCw, AlertCircle, Layers } from "lucide-react";

interface EmptyStateProps {
  title: string;
  description: string;
  actionLabel?: string;
  onAction?: () => void;
  icon?: React.ReactNode;
}

export function EmptyState({
  title,
  description,
  actionLabel,
  onAction,
  icon,
}: EmptyStateProps) {
  return (
    <div className="empty-state-card">
      <div className="empty-state-icon">
        {icon || <Layers size={36} />}
      </div>
      <h3 className="empty-state-title">{title}</h3>
      <p className="empty-state-desc">{description}</p>
      {actionLabel && onAction && (
        <button onClick={onAction} className="btn-primary">
          {actionLabel}
        </button>
      )}
    </div>
  );
}

export function LoadingSkeleton() {
  return (
    <div className="loading-skeleton-wrap">
      <div className="skeleton-hero" />
      <div className="skeleton-grid">
        <div className="skeleton-card" />
        <div className="skeleton-card" />
        <div className="skeleton-card" />
        <div className="skeleton-card" />
      </div>
      <div className="skeleton-large" />
    </div>
  );
}

export function ErrorBanner({
  message,
  onRetry,
}: {
  message: string;
  onRetry?: () => void;
}) {
  return (
    <div className="error-banner">
      <AlertCircle size={20} className="error-icon" />
      <div className="error-content">
        <strong>Error Loading Data</strong>
        <p>{message}</p>
      </div>
      {onRetry && (
        <button onClick={onRetry} className="btn-secondary btn-sm">
          <RefreshCw size={13} /> Retry
        </button>
      )}
    </div>
  );
}
