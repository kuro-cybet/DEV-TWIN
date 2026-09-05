import React, { useState } from "react";
import { HelpCircle } from "lucide-react";

interface MetricCardProps {
  label: string;
  value: string | number;
  subtext?: string;
  tooltip?: string;
  badge?: {
    text: string;
    variant: "success" | "warning" | "danger" | "neutral";
  };
  icon?: React.ReactNode;
}

export function MetricCard({
  label,
  value,
  subtext,
  tooltip,
  badge,
  icon,
}: MetricCardProps) {
  const [showTooltip, setShowTooltip] = useState(false);

  return (
    <div className="metric-card">
      <div className="metric-card-header">
        <div className="metric-label-group">
          <span className="metric-label">{label}</span>
          {tooltip && (
            <div
              className="metric-tooltip-trigger"
              onMouseEnter={() => setShowTooltip(true)}
              onMouseLeave={() => setShowTooltip(false)}
            >
              <HelpCircle size={14} />
              {showTooltip && <div className="metric-tooltip-bubble">{tooltip}</div>}
            </div>
          )}
        </div>
        {icon && <div className="metric-icon-wrap">{icon}</div>}
      </div>

      <div className="metric-card-body">
        <div className="metric-value-row">
          <span className="metric-value">{value}</span>
          {badge && (
            <span className={`metric-badge badge-${badge.variant}`}>
              {badge.text}
            </span>
          )}
        </div>
        {subtext && <span className="metric-subtext">{subtext}</span>}
      </div>
    </div>
  );
}
