# DevTwin

> **Intelligent Software Architecture Visualization & Dependency Intelligence Platform**  
> An automated, static-analysis-driven platform to discover code structure, evaluate coupling metrics, detect circular dependencies, and simulate potential change impact blast radiuses.

---

## 🌟 Overview & Problem Statement

Modern software architectures quickly degrade into complex webs of interconnected modules, hidden cyclic dependencies, and tightly coupled god-classes. Making changes to core components often produces unforeseen regressions downstream.

**DevTwin** bridges the gap between raw source code and architectural comprehension:
1. It ingests software repositories safely (via ZIP upload or GitHub URL).
2. It parses source code using AST into a normalized, single source of truth.
3. It visualizes the high-level architecture and multi-level component trees.
4. It computes graph-theoretic coupling metrics, instability scores, and detects circular dependency chains.
5. It runs change impact simulations to compute the direct and transitive blast radius before making code modifications.

---

## 🏗️ Core Architecture Principle

```
┌─────────────────┐       ┌──────────────────────┐       ┌──────────────────┐       ┌────────────────────────┐
│  Source Ingest  │ ───>  │  Python AST Parser   │ ───>  │  Normalized      │ ───>  │  Module 1: Arch Graph  │
│  (ZIP / GitHub) │       │  (Parse Once)        │       │  Data Model      │       │  Module 2: Deps/Impact │
└─────────────────┘       └──────────────────────┘       └──────────────────┘       └────────────────────────┘
```

**PARSE ONCE → NORMALIZE ONCE → STORE ONCE → REUSE EVERYWHERE**

DevTwin parses the codebase a single time into a unified internal model:
- **Components**: Files, Modules, Classes, Functions, and Methods.
- **Relationships**: Imports, Inheritances, Function Calls, and Containments.

Both **Module 1 (Architecture)** and **Module 2 (Dependencies & Impact)** consume this same unified representation, guaranteeing zero duplicate parsing and absolute consistency.

---

## 📦 Core Modules & Features

### 🏛️ Module 1 — Architecture Discovery & Visualization
- **Multi-Level Representation**: Toggle between high-level **File View** and detailed **Component View** (classes and functions).
- **Interactive Graph**: Zoom, pan, drag nodes, filter by component type, and inspect incoming/outgoing dependencies in a slide-out drawer.
- **Component Inventory**: Structured directory of all discovered modules, classes, and callable functions with line numbers and parent hierarchies.

### 🧠 Module 2 — Dependency Intelligence & Change Impact
- **Coupling Analysis**: Calculates Afferent Coupling ($C_a$, incoming), Efferent Coupling ($C_e$, outgoing), and Martin's Instability Index ($I = \frac{C_e}{C_a + C_e}$). Classifies coupling into `LOW`, `MEDIUM`, and `HIGH` with plain-English rationales.
- **Circular Dependency Detection**: Uses NetworkX Tarjan/Johnson cycle detection to identify all closed cyclic dependency loops ($A \rightarrow B \rightarrow C \rightarrow A$), highlighting involved components and assigning a severity rating.
- **Critical Architectural Hubs**: Ranks structural bottlenecks by combining Betweenness Centrality with in-degree connectivity.
- **Potential Change Impact Analysis**: Given any target component, traverses the reversed dependency graph to compute:
  - **Direct Impact (Depth 1)**: Immediate callers and importers.
  - **Indirect Impact (Depth 2+)**: Transitive downstream dependents.
  - **Total Blast Radius**: Percentage of the codebase impacted.
  - **Visualized Impact Subgraph**: Color-coded reachability graph.
- **Architectural Health Reports**: Produces an overall health score (0–100, Grade A–F) with top risks and actionable refactoring recommendations.

---

## 🛠️ Technology Stack

| Layer | Technology |
|---|---|
| **Frontend** | React 19, TypeScript, Vite, React Router, Lucide Icons |
| **Backend** | Python 3.11+, FastAPI, Pydantic v2, Uvicorn |
| **Graph & Analysis** | NetworkX, Python AST |
| **Database** | PostgreSQL (Production/Docker), SQLite (Zero-config local fallback) |
| **Testing** | Pytest, TestClient, Vitest |
| **DevOps / Deployment**| Docker, Docker Compose, Vercel, Render |

---

## 📁 Repository Folder Structure

```
devtwin/
├── backend/
│   ├── app/
│   │   ├── core/           # Config, Database (PostgreSQL / SQLite)
│   │   ├── models/         # Project, Component, Relationship models
│   │   ├── schemas/        # Pydantic schemas (Project, Arch, Deps, Impact, Reports)
│   │   ├── repositories/   # Persistence layer (PostgreSQL / SQLite)
│   │   ├── analyzers/      # Code analysis algorithms
│   │   │   ├── parsers/    # Python AST parser
│   │   │   ├── architecture/ # Multi-level graph builder
│   │   │   ├── dependencies/ # NetworkX metrics, coupling & cycle detection
│   │   │   ├── impact/     # Potential change impact graph traversal
│   │   │   └── reports/    # Health summary & diagnostic generator
│   │   ├── services/       # Ingestion, project, and analysis business logic
│   │   └── api/
│   │       ├── router.py   # /api/v1 router aggregation
│   │       └── routes/     # health, projects, architecture, dependencies, impact, reports
│   ├── tests/              # Pytest test suite (API, parser, analysis, ingestion)
│   ├── Dockerfile
│   └── requirements.txt
├── frontend/
│   ├── src/
│   │   ├── app/            # React App & Routes
│   │   ├── components/     # InteractiveGraph, Layout, MetricCard, EmptyState
│   │   ├── pages/          # ProjectsList, ProjectOverview, ArchitectureView, DependenciesView, ImpactAnalysisView, ReportsView
│   │   ├── services/api/   # Centralized API client modules
│   │   └── styles.css      # Clean, minimal black-and-white theme
│   ├── Dockerfile
│   ├── package.json
│   ├── vercel.json         # Vercel SPA routing rewrites
│   └── vite.config.ts
├── docker-compose.yml       # Local orchestration (Postgres + Backend + Frontend)
├── render.yaml             # Render deployment blueprint
├── .env.example
└── README.md
```

---

## 🚀 Local Development Setup

### Option 1: Docker Compose (Fastest)

```bash
docker-compose up --build
```
- **Frontend**: `http://localhost:5173`
- **Backend API**: `http://localhost:8000`
- **API Docs (Swagger)**: `http://localhost:8000/docs`
- **PostgreSQL**: `localhost:5432`

---

### Option 2: Manual Local Setup

#### 1. Backend Setup
```bash
cd backend
python -m venv .venv

# Windows:
.venv\Scripts\activate
# macOS/Linux:
source .venv/bin/activate

pip install -r requirements.txt
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

#### 2. Frontend Setup
```bash
cd frontend
npm install
npm run dev
```
Open `http://localhost:5173` in your browser.

---

## ⚙️ Environment Variables

Create a `.env` file or export the following variables:

```bash
# Backend
PORT=8000
HOST=0.0.0.0
# SQLite fallback (default):
DATABASE_URL=sqlite:///./devtwin.db
# PostgreSQL (Production):
# DATABASE_URL=postgresql://devtwin:devtwin@localhost:5432/devtwin
CORS_ORIGINS=http://localhost:5173,http://localhost:3000

# Frontend
VITE_API_URL=http://localhost:8000/api/v1
```

---

## 📡 REST API Reference (`/api/v1`)

### Projects
- `POST   /api/v1/projects` — Create project from JSON metadata
- `POST   /api/v1/projects/upload` — Ingest project from multipart ZIP archive
- `GET    /api/v1/projects` — List all projects
- `GET    /api/v1/projects/{project_id}` — Get project metadata & counts
- `DELETE /api/v1/projects/{project_id}` — Delete project and associated analyses
- `POST   /api/v1/projects/{project_id}/analyze` — Run unified AST analysis & build graphs

### Architecture (Module 1)
- `GET    /api/v1/projects/{project_id}/architecture` — Architecture summary
- `GET    /api/v1/projects/{project_id}/architecture/components` — List all detected components
- `GET    /api/v1/projects/{project_id}/architecture/graph?level=file|component` — Interactive graph data

### Dependencies & Intelligence (Module 2)
- `GET    /api/v1/projects/{project_id}/dependencies` — Dependency overview
- `GET    /api/v1/projects/{project_id}/dependencies/graph` — Directed dependency graph
- `GET    /api/v1/projects/{project_id}/dependencies/metrics` — Coupling and centrality metrics
- `GET    /api/v1/projects/{project_id}/dependencies/cycles` — Circular dependency paths and severity

### Change Impact Analysis (Module 2)
- `GET    /api/v1/projects/{project_id}/impact/{component_id}` — Compute potential change impact
- `POST   /api/v1/projects/{project_id}/impact/analyze` — Run change impact simulation

### Reports & Diagnostics
- `GET    /api/v1/projects/{project_id}/reports/summary` — Health score, grade, risks & recommendations
- `GET    /api/v1/projects/{project_id}/reports/architecture` — Module and class distribution report
- `GET    /api/v1/projects/{project_id}/reports/dependencies` — Complete coupling and diagnostic report

---

## 🧪 Running Tests

### Backend Tests (Pytest)
```bash
cd backend
python -m pytest tests -v
```
All unit and integration tests verify:
- API CRUD and health check endpoints
- Real Python AST parsing (classes, methods, functions, relative/absolute imports, inheritance)
- NetworkX graph creation and metric calculation
- Circular dependency cycle detection
- Potential change impact calculation (direct vs indirect)
- Safe ZIP extraction and Zip Slip prevention

---

## 🚢 Production Deployment

### Frontend → Vercel
1. Connect repository to [Vercel](https://vercel.com).
2. Set Root Directory to `frontend`.
3. Build Command: `npm run build`
4. Output Directory: `dist`
5. Environment Variable: `VITE_API_URL=https://your-backend-url.onrender.com/api/v1`
6. `vercel.json` rewrites are included for React Router SPA navigation.

### Backend → Render
1. Connect repository to [Render](https://render.com).
2. Use the provided `render.yaml` or create a Web Service:
   - Root Directory: `backend`
   - Build Command: `pip install -r requirements.txt`
   - Start Command: `uvicorn app.main:app --host 0.0.0.0 --port $PORT`
   - Environment Variables: `DATABASE_URL` (from Render PostgreSQL), `CORS_ORIGINS` (Vercel URL).

---

## 🔬 Analysis Methodology Deep Dive

### 1. Coupling Metrics
- **Afferent Coupling ($C_a$)**: Number of external components depending on this component (fan-in).
- **Efferent Coupling ($C_e$)**: Number of external components this component depends on (fan-out).
- **Instability Index ($I$)**: $I = \frac{C_e}{C_a + C_e}$. Measures component volatility ($0 = \text{stable}$, $1 = \text{unstable}$).

### 2. Circular Dependency Detection
Using NetworkX simple cycle detection (`nx.simple_cycles(G)`), DevTwin traces elementary directed cycles in the import graph. Cycles create mutual lock-in that degrades unit testing isolation and modularity.

### 3. Potential Change Impact Traversal
When component $X$ is selected, DevTwin reverses the directed dependency graph and executes breadth-first traversal (BFS).
- **Direct Impact**: Components at distance $1$ (immediate callers).
- **Indirect Impact**: Components at distance $\ge 2$ (transitive dependencies).
*Disclaimer*: Static reachability represents maximum potential blast radius; dynamic conditional branching may prune runtime impact.

---

## 🔒 Security Measures
- **No Code Execution**: DevTwin never executes analyzed source code. Analysis is purely static via Python AST.
- **Zip Slip Protection**: Ingestion enforces strict path boundary validation preventing directory traversal attacks.
- **Directory Filtering**: Ingestion automatically strips `.git`, `node_modules`, `.venv`, and `__pycache__`.

---

## 📄 License
MIT License. Built for Software Engineering Capstone.
