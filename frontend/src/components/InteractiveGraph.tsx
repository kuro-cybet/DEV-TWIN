import React, { useState, useEffect, useRef, useMemo } from "react";
import { ZoomIn, ZoomOut, Maximize2, Search, Filter, Layers, Info, X } from "lucide-react";
import { ComponentNode, RelationshipEdge } from "../services/api/types";

interface InteractiveGraphProps {
  data: {
    nodes: ComponentNode[];
    edges: RelationshipEdge[];
  } | null;
  selectedNodeId?: string;
  onSelectNode?: (node: ComponentNode | null) => void;
  highlightDirectIds?: string[];
  highlightIndirectIds?: string[];
  highlightCyclePaths?: string[][];
  title?: string;
  emptyMessage?: string;
}

interface NodePosition {
  x: number;
  y: number;
  vx: number;
  vy: number;
}

export function InteractiveGraph({
  data,
  selectedNodeId,
  onSelectNode,
  highlightDirectIds = [],
  highlightIndirectIds = [],
  highlightCyclePaths = [],
  title = "Architecture & Dependency Graph",
  emptyMessage = "No graph data available. Run analysis on this project to view components.",
}: InteractiveGraphProps) {
  const [zoom, setZoom] = useState(1);
  const [pan, setPan] = useState({ x: 0, y: 0 });
  const [isDraggingCanvas, setIsDraggingCanvas] = useState(false);
  const [dragStart, setDragStart] = useState({ x: 0, y: 0 });
  const [searchQuery, setSearchQuery] = useState("");
  const [typeFilter, setTypeFilter] = useState<string>("all");
  const [selectedNode, setSelectedNode] = useState<ComponentNode | null>(null);
  const [draggingNodeId, setDraggingNodeId] = useState<string | null>(null);

  const containerRef = useRef<HTMLDivElement>(null);

  // Filter nodes based on search and type
  const filteredNodes = useMemo(() => {
    if (!data?.nodes) return [];
    return data.nodes.filter((node) => {
      const matchesSearch =
        searchQuery.trim() === "" ||
        node.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
        node.id.toLowerCase().includes(searchQuery.toLowerCase());
      const matchesType = typeFilter === "all" || node.type === typeFilter;
      return matchesSearch && matchesType;
    });
  }, [data?.nodes, searchQuery, typeFilter]);

  const filteredNodeIds = useMemo(() => new Set(filteredNodes.map((n) => n.id)), [filteredNodes]);

  const filteredEdges = useMemo(() => {
    if (!data?.edges) return [];
    return data.edges.filter(
      (e) => filteredNodeIds.has(e.source) && filteredNodeIds.has(e.target)
    );
  }, [data?.edges, filteredNodeIds]);

  // Set of edges in cycles for red highlighting
  const cycleEdges = useMemo(() => {
    const set = new Set<string>();
    highlightCyclePaths.forEach((path) => {
      for (let i = 0; i < path.length - 1; i++) {
        set.add(`${path[i]}->${path[i + 1]}`);
      }
      if (path.length > 1) {
        set.add(`${path[path.length - 1]}->${path[0]}`);
      }
    });
    return set;
  }, [highlightCyclePaths]);

  // Node Positions using a layered circular/force grid layout
  const [positions, setPositions] = useState<Record<string, NodePosition>>({});

  useEffect(() => {
    if (!data?.nodes || data.nodes.length === 0) return;

    const newPositions: Record<string, NodePosition> = {};
    const count = data.nodes.length;
    const width = 800;
    const height = 550;
    const cols = Math.ceil(Math.sqrt(count * 1.5));
    const spacingX = Math.max(160, width / cols);
    const spacingY = 120;

    // Group nodes by type or parent folder
    const groups: Record<string, ComponentNode[]> = {};
    data.nodes.forEach((n) => {
      const grp = n.parent || "root";
      if (!groups[grp]) groups[grp] = [];
      groups[grp].push(n);
    });

    let currentX = 80;
    let currentY = 80;
    let maxRowHeight = 0;

    Object.values(groups).forEach((groupNodes) => {
      groupNodes.forEach((node, i) => {
        newPositions[node.id] = {
          x: currentX + (i % 4) * spacingX,
          y: currentY + Math.floor(i / 4) * spacingY,
          vx: 0,
          vy: 0,
        };
      });
      currentY += Math.ceil(groupNodes.length / 4) * spacingY + 40;
    });

    setPositions(newPositions);
  }, [data?.nodes]);

  // Handle Canvas Drag / Pan
  const handleMouseDown = (e: React.MouseEvent) => {
    if ((e.target as HTMLElement).closest(".graph-node")) return;
    setIsDraggingCanvas(true);
    setDragStart({ x: e.clientX - pan.x, y: e.clientY - pan.y });
  };

  const handleMouseMove = (e: React.MouseEvent) => {
    if (isDraggingCanvas) {
      setPan({ x: e.clientX - dragStart.x, y: e.clientY - dragStart.y });
    } else if (draggingNodeId && containerRef.current) {
      const rect = containerRef.current.getBoundingClientRect();
      const mouseX = (e.clientX - rect.left - pan.x) / zoom;
      const mouseY = (e.clientY - rect.top - pan.y) / zoom;
      setPositions((prev) => ({
        ...prev,
        [draggingNodeId]: { ...prev[draggingNodeId], x: mouseX, y: mouseY },
      }));
    }
  };

  const handleMouseUp = () => {
    setIsDraggingCanvas(false);
    setDraggingNodeId(null);
  };

  const handleZoom = (delta: number) => {
    setZoom((prev) => Math.min(Math.max(prev + delta, 0.3), 2.5));
  };

  const resetView = () => {
    setZoom(1);
    setPan({ x: 0, y: 0 });
  };

  const handleNodeClick = (node: ComponentNode) => {
    setSelectedNode(node);
    onSelectNode?.(node);
  };

  if (!data || !data.nodes || data.nodes.length === 0) {
    return (
      <div className="graph-empty-container">
        <Layers className="graph-empty-icon" />
        <p className="graph-empty-text">{emptyMessage}</p>
      </div>
    );
  }

  return (
    <div className="interactive-graph-wrapper">
      {/* Graph Toolbar */}
      <div className="graph-toolbar">
        <div className="graph-toolbar-title">
          <span>{title}</span>
          <span className="graph-node-count">{filteredNodes.length} nodes · {filteredEdges.length} edges</span>
        </div>

        <div className="graph-toolbar-controls">
          <div className="graph-search-box">
            <Search size={14} />
            <input
              type="text"
              placeholder="Search components..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
            {searchQuery && (
              <button onClick={() => setSearchQuery("")} className="clear-btn">
                <X size={12} />
              </button>
            )}
          </div>

          <div className="graph-filter-select">
            <Filter size={14} />
            <select value={typeFilter} onChange={(e) => setTypeFilter(e.target.value)}>
              <option value="all">All Types</option>
              <option value="file">Files</option>
              <option value="class">Classes</option>
              <option value="function">Functions</option>
              <option value="method">Methods</option>
            </select>
          </div>

          <div className="graph-zoom-controls">
            <button onClick={() => handleZoom(0.15)} title="Zoom In">
              <ZoomIn size={15} />
            </button>
            <button onClick={() => handleZoom(-0.15)} title="Zoom Out">
              <ZoomOut size={15} />
            </button>
            <button onClick={resetView} title="Reset View">
              <Maximize2 size={15} />
            </button>
          </div>
        </div>
      </div>

      {/* SVG Interactive Canvas */}
      <div
        className="graph-canvas-container"
        ref={containerRef}
        onMouseDown={handleMouseDown}
        onMouseMove={handleMouseMove}
        onMouseUp={handleMouseUp}
        style={{ cursor: isDraggingCanvas ? "grabbing" : "grab" }}
      >
        <svg className="graph-svg-layer" width="100%" height="100%">
          <defs>
            <marker
              id="arrowhead-default"
              markerWidth="8"
              markerHeight="6"
              refX="16"
              refY="3"
              orient="auto"
            >
              <polygon points="0 0, 8 3, 0 6" fill="#9ca3af" />
            </marker>
            <marker
              id="arrowhead-cycle"
              markerWidth="9"
              markerHeight="7"
              refX="16"
              refY="3.5"
              orient="auto"
            >
              <polygon points="0 0, 9 3.5, 0 7" fill="#ef4444" />
            </marker>
            <marker
              id="arrowhead-impact-direct"
              markerWidth="9"
              markerHeight="7"
              refX="16"
              refY="3.5"
              orient="auto"
            >
              <polygon points="0 0, 9 3.5, 0 7" fill="#f97316" />
            </marker>
            <marker
              id="arrowhead-impact-indirect"
              markerWidth="9"
              markerHeight="7"
              refX="16"
              refY="3.5"
              orient="auto"
            >
              <polygon points="0 0, 9 3.5, 0 7" fill="#818cf8" />
            </marker>
          </defs>

          <g transform={`translate(${pan.x}, ${pan.y}) scale(${zoom})`}>
            {/* Edge lines */}
            {filteredEdges.map((edge) => {
              const srcPos = positions[edge.source];
              const tgtPos = positions[edge.target];
              if (!srcPos || !tgtPos) return null;

              const isCycleEdge = cycleEdges.has(`${edge.source}->${edge.target}`);
              const isDirectImpact = highlightDirectIds.includes(edge.source) || highlightDirectIds.includes(edge.target);
              const isIndirectImpact = highlightIndirectIds.includes(edge.source) || highlightIndirectIds.includes(edge.target);

              let strokeColor = "#d1d5db";
              let strokeWidth = 1.5;
              let markerEnd = "url(#arrowhead-default)";

              if (isCycleEdge) {
                strokeColor = "#ef4444";
                strokeWidth = 2.5;
                markerEnd = "url(#arrowhead-cycle)";
              } else if (isDirectImpact) {
                strokeColor = "#f97316";
                strokeWidth = 2.2;
                markerEnd = "url(#arrowhead-impact-direct)";
              } else if (isIndirectImpact) {
                strokeColor = "#818cf8";
                strokeWidth = 2;
                markerEnd = "url(#arrowhead-impact-indirect)";
              }

              return (
                <g key={edge.id || `${edge.source}->${edge.target}`}>
                  <line
                    x1={srcPos.x + 75}
                    y1={srcPos.y + 25}
                    x2={tgtPos.x + 75}
                    y2={tgtPos.y + 25}
                    stroke={strokeColor}
                    strokeWidth={strokeWidth}
                    strokeDasharray={edge.type === "call" ? "4 3" : undefined}
                    markerEnd={markerEnd}
                  />
                </g>
              );
            })}

            {/* Nodes */}
            {filteredNodes.map((node) => {
              const pos = positions[node.id] || { x: 50, y: 50 };
              const isSelected = selectedNodeId === node.id || selectedNode?.id === node.id;
              const isDirectImpact = highlightDirectIds.includes(node.id);
              const isIndirectImpact = highlightIndirectIds.includes(node.id);
              const isCycleInvolved = highlightCyclePaths.some((p) => p.includes(node.id));

              let badgeClass = "badge-file";
              if (node.type === "class") badgeClass = "badge-class";
              if (node.type === "function" || node.type === "method") badgeClass = "badge-function";

              let nodeStateClass = "";
              if (isSelected) nodeStateClass = "node-selected";
              else if (isDirectImpact) nodeStateClass = "node-impact-direct";
              else if (isIndirectImpact) nodeStateClass = "node-impact-indirect";
              else if (isCycleInvolved) nodeStateClass = "node-cycle";

              return (
                <foreignObject
                  key={node.id}
                  x={pos.x}
                  y={pos.y}
                  width={160}
                  height={65}
                  className="graph-node-foreign"
                >
                  <div
                    className={`graph-node ${nodeStateClass}`}
                    onClick={() => handleNodeClick(node)}
                    onMouseDown={(e) => {
                      e.stopPropagation();
                      setDraggingNodeId(node.id);
                    }}
                  >
                    <div className="graph-node-header">
                      <span className={`node-badge ${badgeClass}`}>{node.type}</span>
                      {node.line_number && <span className="node-line">L{node.line_number}</span>}
                    </div>
                    <div className="graph-node-name" title={node.id}>
                      {node.name}
                    </div>
                    <div className="graph-node-path" title={node.file_path}>
                      {node.file_path}
                    </div>
                  </div>
                </foreignObject>
              );
            })}
          </g>
        </svg>

        {/* Selected Node Details Drawer */}
        {selectedNode && (
          <div className="node-details-drawer">
            <div className="node-drawer-header">
              <div className="node-drawer-title">
                <span className="drawer-eyebrow">{selectedNode.type.toUpperCase()} DETAILS</span>
                <h3>{selectedNode.name}</h3>
              </div>
              <button onClick={() => setSelectedNode(null)} className="drawer-close-btn">
                <X size={16} />
              </button>
            </div>

            <div className="node-drawer-body">
              <div className="drawer-field">
                <span className="drawer-label">Identifier</span>
                <code className="drawer-value-code">{selectedNode.id}</code>
              </div>

              <div className="drawer-field">
                <span className="drawer-label">File Path</span>
                <span className="drawer-value">{selectedNode.file_path}</span>
              </div>

              {selectedNode.line_number && (
                <div className="drawer-field">
                  <span className="drawer-label">Line Number</span>
                  <span className="drawer-value">Line {selectedNode.line_number}</span>
                </div>
              )}

              {selectedNode.parent && (
                <div className="drawer-field">
                  <span className="drawer-label">Parent Component</span>
                  <span className="drawer-value">{selectedNode.parent}</span>
                </div>
              )}

              <div className="drawer-connections">
                <h4>Dependencies</h4>
                <div className="drawer-conn-list">
                  <div className="conn-section">
                    <span className="conn-title">Outgoing (Imports/Calls)</span>
                    {data.edges.filter((e) => e.source === selectedNode.id).length === 0 ? (
                      <span className="conn-empty">None</span>
                    ) : (
                      data.edges
                        .filter((e) => e.source === selectedNode.id)
                        .map((e) => (
                          <div key={e.id} className="conn-item">
                            <span>→ {e.target}</span>
                            <span className="conn-type">{e.type}</span>
                          </div>
                        ))
                    )}
                  </div>

                  <div className="conn-section">
                    <span className="conn-title">Incoming (Depended on by)</span>
                    {data.edges.filter((e) => e.target === selectedNode.id).length === 0 ? (
                      <span className="conn-empty">None</span>
                    ) : (
                      data.edges
                        .filter((e) => e.target === selectedNode.id)
                        .map((e) => (
                          <div key={e.id} className="conn-item">
                            <span>← {e.source}</span>
                            <span className="conn-type">{e.type}</span>
                          </div>
                        ))
                    )}
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
