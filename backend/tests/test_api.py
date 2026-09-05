import pytest
from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)

def test_health():
    response = client.get("/api/v1/health")
    assert response.status_code == 200
    data = response.json()
    assert data["status"] == "ok"
    assert "DevTwin" in data["service"]

def test_root():
    response = client.get("/")
    assert response.status_code == 200
    assert response.json()["name"] == "DevTwin API"

def test_project_crud():
    # 1. Create project
    create_resp = client.post("/api/v1/projects", json={"name": "Test Project Alpha"})
    assert create_resp.status_code == 201
    project_data = create_resp.json()
    project_id = project_data["id"]
    assert project_data["name"] == "Test Project Alpha"
    assert project_data["status"] == "created"

    # 2. Get project
    get_resp = client.get(f"/api/v1/projects/{project_id}")
    assert get_resp.status_code == 200
    assert get_resp.json()["id"] == project_id

    # 3. List projects
    list_resp = client.get("/api/v1/projects")
    assert list_resp.status_code == 200
    projects = list_resp.json()
    assert any(p["id"] == project_id for p in projects)

    # 4. Delete project
    del_resp = client.delete(f"/api/v1/projects/{project_id}")
    assert del_resp.status_code == 200

    # 5. Verify deleted
    get_del_resp = client.get(f"/api/v1/projects/{project_id}")
    assert get_del_resp.status_code == 404

def test_invalid_project_endpoints():
    fake_id = "non-existent-id-9999"
    assert client.get(f"/api/v1/projects/{fake_id}").status_code == 404
    assert client.post(f"/api/v1/projects/{fake_id}/analyze").status_code == 404
    assert client.get(f"/api/v1/projects/{fake_id}/architecture/graph").status_code == 404
    assert client.get(f"/api/v1/projects/{fake_id}/dependencies/metrics").status_code == 404
    assert client.get(f"/api/v1/projects/{fake_id}/dependencies/cycles").status_code == 404
    assert client.get(f"/api/v1/projects/{fake_id}/impact/some_node").status_code == 404
    assert client.get(f"/api/v1/projects/{fake_id}/reports/summary").status_code == 404
