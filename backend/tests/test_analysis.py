import pytest
from app.analyzers.dependencies.analyzer import DependencyAnalyzer
from app.analyzers.impact.analyzer import ImpactAnalyzer
from app.analyzers.reports.generator import report_generator

def test_dependency_analyzer_and_cycles():
    # Construct a test graph with a cycle: A -> B -> C -> A, and D -> A
    graph_data = {
        "nodes": [
            {"id": "service_a.py", "name": "service_a.py", "type": "file"},
            {"id": "service_b.py", "name": "service_b.py", "type": "file"},
            {"id": "service_c.py", "name": "service_c.py", "type": "file"},
            {"id": "client_d.py", "name": "client_d.py", "type": "file"},
        ],
        "edges": [
            {"id": "e1", "source": "service_a.py", "target": "service_b.py", "type": "import"},
            {"id": "e2", "source": "service_b.py", "target": "service_c.py", "type": "import"},
            {"id": "e3", "source": "service_c.py", "target": "service_a.py", "type": "import"},
            {"id": "e4", "source": "client_d.py", "target": "service_a.py", "type": "import"},
        ]
    }

    analyzer = DependencyAnalyzer(graph_data)
    
    # 1. Test Cycles
    cycles_res = analyzer.cycles()
    assert cycles_res["count"] == 1
    assert len(cycles_res["cycles"]) == 1
    assert set(cycles_res["involved_components"]) == {"service_a.py", "service_b.py", "service_c.py"}
    assert cycles_res["severity"] in ("LOW", "MEDIUM", "HIGH", "CRITICAL")

    # 2. Test Metrics
    metrics_res = analyzer.metrics()
    assert metrics_res["components"] == 4
    assert metrics_res["dependencies"] == 4
    assert metrics_res["density"] > 0
    assert len(metrics_res["high_coupling"]) == 4

    # Check coupling levels
    a_coupling = next(c for c in metrics_res["high_coupling"] if c["component"] == "service_a.py")
    assert a_coupling["incoming"] == 2  # from C and D
    assert a_coupling["outgoing"] == 1  # to B
    assert a_coupling["total"] == 3
    assert a_coupling["coupling_level"] in ("LOW", "MEDIUM", "HIGH")
    assert len(a_coupling["explanation"]) > 0

    # 3. Test Critical Components
    crit = metrics_res["critical_components"]
    assert len(crit) > 0
    assert crit[0]["score"] > 0

def test_change_impact_analyzer():
    # Architecture:
    # database.py
    #    ^
    # auth.py (imports database.py)
    #    ^
    # user_service.py (imports auth.py)
    #    ^
    # api_gateway.py (imports user_service.py)
    graph_data = {
        "nodes": [
            {"id": "database.py", "name": "database.py", "type": "file"},
            {"id": "auth.py", "name": "auth.py", "type": "file"},
            {"id": "user_service.py", "name": "user_service.py", "type": "file"},
            {"id": "api_gateway.py", "name": "api_gateway.py", "type": "file"},
        ],
        "edges": [
            {"id": "e1", "source": "auth.py", "target": "database.py", "type": "import"},
            {"id": "e2", "source": "user_service.py", "target": "auth.py", "type": "import"},
            {"id": "e3", "source": "api_gateway.py", "target": "user_service.py", "type": "import"},
        ]
    }

    impact_analyzer = ImpactAnalyzer(graph_data)

    # When database.py changes:
    # Direct impact: auth.py
    # Indirect impact: user_service.py, api_gateway.py
    # Total affected: 3
    res = impact_analyzer.analyze("database.py")
    assert res["selected_component"] == "database.py"
    assert res["total_affected"] == 3
    assert res["direct_count"] == 1
    assert res["indirect_count"] == 2
    assert "auth.py" in res["direct_impact"]
    assert "user_service.py" in res["indirect_impact"]
    assert "api_gateway.py" in res["indirect_impact"]
    assert "POTENTIAL CHANGE IMPACT" in res["disclaimer"]
    assert len(res["impact_graph"]["nodes"]) == 4

    # Invalid component test
    with pytest.raises(KeyError):
        impact_analyzer.analyze("nonexistent.py")

def test_reports_generator():
    metrics = {
        "components": 5,
        "dependencies": 8,
        "density": 0.4,
        "high_coupling": [
            {"component": "core.py", "incoming": 4, "outgoing": 2, "total": 6, "coupling_level": "HIGH", "instability": 0.33}
        ],
        "critical_components": [
            {"component": "core.py", "level": "HIGH", "score": 8.5}
        ]
    }
    cycles = {"count": 1, "cycles": [["a.py", "b.py", "a.py"]], "involved_components": ["a.py", "b.py"]}

    report = report_generator.generate_health_summary("proj_1", "TestProj", metrics, cycles)
    assert report["health_score"] >= 0 and report["health_score"] <= 100
    assert report["health_grade"] in ("A", "B", "C", "D", "F")
    assert len(report["top_risks"]) > 0
    assert len(report["recommendations"]) > 0
