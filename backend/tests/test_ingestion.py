import io
import zipfile
import pytest
from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)

def test_zip_upload_and_analysis_workflow():
    # 1. Create in-memory test ZIP file
    zip_buf = io.BytesIO()
    with zipfile.ZipFile(zip_buf, "w", zipfile.ZIP_DEFLATED) as zf:
        zf.writestr("app/__init__.py", "")
        zf.writestr("app/config.py", "DATABASE_URL = 'sqlite://'")
        zf.writestr("app/models.py", """
from app.config import DATABASE_URL

class User:
    def __init__(self, name):
        self.name = name
""")
        zf.writestr("app/services.py", """
from app.models import User

class UserService:
    def get_user(self):
        return User('Bob')
""")
        zf.writestr("app/main.py", """
from app.services import UserService

def run():
    svc = UserService()
    return svc.get_user()
""")

    zip_bytes = zip_buf.getvalue()

    # 2. Upload ZIP via multipart/form-data
    response = client.post(
        "/api/v1/projects/upload",
        data={"name": "Sample Architecture Project"},
        files={"file": ("project.zip", zip_bytes, "application/zip")},
    )
    assert response.status_code == 201
    proj_data = response.json()
    project_id = proj_data["id"]
    assert proj_data["status"] == "uploaded"

    # 3. Analyze the project
    analyze_resp = client.post(f"/api/v1/projects/{project_id}/analyze")
    assert analyze_resp.status_code == 200
    res = analyze_resp.json()
    assert res["status"] == "analyzed"
    assert res["components"] > 0
    assert res["dependencies"] > 0

    # 4. Architecture Graph
    arch_resp = client.get(f"/api/v1/projects/{project_id}/architecture/graph")
    assert arch_resp.status_code == 200
    arch_data = arch_resp.json()
    assert len(arch_data["nodes"]) >= 4
    assert len(arch_data["edges"]) >= 3

    # 5. Architecture Components
    comp_resp = client.get(f"/api/v1/projects/{project_id}/architecture/components")
    assert comp_resp.status_code == 200
    comp_data = comp_resp.json()
    assert comp_data["total"] >= 4

    # 6. Dependency Metrics
    metrics_resp = client.get(f"/api/v1/projects/{project_id}/dependencies/metrics")
    assert metrics_resp.status_code == 200
    metrics_data = metrics_resp.json()
    assert metrics_data["components"] >= 4
    assert len(metrics_data["high_coupling"]) > 0

    # 7. Cycles
    cycles_resp = client.get(f"/api/v1/projects/{project_id}/dependencies/cycles")
    assert cycles_resp.status_code == 200
    assert cycles_resp.json()["count"] == 0  # No cycles in this acyclic app

    # 8. Potential Change Impact on config.py
    impact_resp = client.get(f"/api/v1/projects/{project_id}/impact/app/config.py")
    assert impact_resp.status_code == 200
    impact_data = impact_resp.json()
    assert impact_data["total_affected"] >= 1
    assert "POTENTIAL CHANGE IMPACT" in impact_data["disclaimer"]

    # 9. Health Summary Report
    summary_resp = client.get(f"/api/v1/projects/{project_id}/reports/summary")
    assert summary_resp.status_code == 200
    summary_data = summary_resp.json()
    assert summary_data["health_score"] >= 70
    assert summary_data["health_grade"] in ("A", "B", "C")

    # 10. Architecture Report
    arch_rep_resp = client.get(f"/api/v1/projects/{project_id}/reports/architecture")
    assert arch_rep_resp.status_code == 200
    assert arch_rep_resp.json()["total_files"] >= 4

    # 11. Dependency Report
    dep_rep_resp = client.get(f"/api/v1/projects/{project_id}/reports/dependencies")
    assert dep_rep_resp.status_code == 200
    assert dep_rep_resp.json()["total_dependencies"] >= 3
