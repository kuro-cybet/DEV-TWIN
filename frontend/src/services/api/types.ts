export interface Project {
  id: string;
  name: string;
  repository_url?: string | null;
  source_path?: string | null;
  status: 'created' | 'uploaded' | 'analyzed' | 'failed' | 'awaiting_source';
  created_at?: string;
  updated_at?: string;
  components_count?: number;
  dependencies_count?: number;
}

export interface ComponentNode {
  id: string;
  name: string;
  type: 'file' | 'module' | 'class' | 'function' | 'method';
  file_path: string;
  line_number?: number | null;
  language?: string;
  parent?: string | null;
  metadata?: Record<string, any>;
}

export interface RelationshipEdge {
  id: string;
  source: string;
  target: string;
  type: 'import' | 'inheritance' | 'call' | 'contains';
  confidence?: number;
  line_number?: number | null;
  metadata?: Record<string, any>;
}

export interface GraphData {
  nodes: ComponentNode[];
  edges: RelationshipEdge[];
  total_nodes?: number;
  total_edges?: number;
  classes_count?: number;
  functions_count?: number;
  modules_count?: number;
}

export interface CouplingItem {
  component: string;
  name: string;
  type: string;
  incoming: number;
  outgoing: number;
  total: number;
  instability: number;
  coupling_level: 'LOW' | 'MEDIUM' | 'HIGH';
  explanation: string;
}

export interface CriticalComponentItem {
  component: string;
  name: string;
  type: string;
  score: number;
  level: 'HIGH' | 'MEDIUM' | 'LOW';
  incoming: number;
  outgoing: number;
  betweenness: number;
  explanation: string;
}

export interface DependencyMetrics {
  components: number;
  dependencies: number;
  density: number;
  density_percentage: number;
  avg_dependencies_per_component: number;
  high_coupling: CouplingItem[];
  critical_components: CriticalComponentItem[];
  coupling_distribution: Record<string, number>;
}

export interface CyclesData {
  count: number;
  cycles: string[][];
  involved_components: string[];
  severity: 'NONE' | 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
}

export interface ImpactNode {
  id: string;
  name: string;
  type: string;
  file_path: string;
  depth: number;
  impact_type: 'direct' | 'indirect';
}

export interface ImpactResult {
  selected_component: string;
  selected_component_name: string;
  total_affected: number;
  direct_count: number;
  indirect_count: number;
  direct_impact: string[];
  indirect_impact: string[];
  direct_impact_nodes: ImpactNode[];
  indirect_impact_nodes: ImpactNode[];
  affected_components: string[];
  impact_graph: GraphData;
  disclaimer: string;
}

export interface HealthSummaryReport {
  project_id: string;
  project_name: string;
  health_score: number;
  health_grade: string;
  summary: string;
  metrics: DependencyMetrics;
  circular_dependencies: CyclesData;
  critical_components: CriticalComponentItem[];
  top_risks: Array<{ severity: string; title: string; description: string }>;
  recommendations: string[];
  generated_at: string;
}

export interface ArchitectureReport {
  project_id: string;
  project_name: string;
  total_files: number;
  total_modules: number;
  total_classes: number;
  total_functions: number;
  total_components: number;
  modules_breakdown: Array<{ module: string; files_count: number; files: string[] }>;
  inheritance_hierarchies: Array<{ derived: string; base: string; confidence: number }>;
  components_by_type: Record<string, number>;
  generated_at: string;
}

export interface DependencyReport {
  project_id: string;
  project_name: string;
  total_dependencies: number;
  graph_density: number;
  cycles_count: number;
  cycles: string[][];
  coupling_analysis: CouplingItem[];
  critical_components: CriticalComponentItem[];
  instability_index_avg: number;
  generated_at: string;
}
