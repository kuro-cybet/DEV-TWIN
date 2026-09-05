import os
import tempfile
from pathlib import Path
from app.analyzers.parsers.python import PythonParser

def test_python_parser_ast():
    with tempfile.TemporaryDirectory() as tmpdir:
        root = Path(tmpdir)
        
        # Create sample project structure:
        # root/
        #   models/
        #     base.py
        #     user.py
        #   services/
        #     auth.py
        #   main.py
        (root / "models").mkdir(parents=True)
        (root / "services").mkdir(parents=True)

        (root / "models" / "base.py").write_text("""
class BaseModel:
    def to_dict(self):
        return {}
""", encoding="utf-8")

        (root / "models" / "user.py").write_text("""
from .base import BaseModel

class User(BaseModel):
    def __init__(self, username: str):
        self.username = username
""", encoding="utf-8")

        (root / "services" / "auth.py").write_text("""
from models.user import User

class AuthService:
    def authenticate(self, username: str) -> User:
        return User(username)
""", encoding="utf-8")

        (root / "main.py").write_text("""
from services.auth import AuthService

def start_app():
    auth = AuthService()
    user = auth.authenticate("alice")
    return user
""", encoding="utf-8")

        parser = PythonParser()
        parsed = parser.parse(str(root))

        assert len(parsed.nodes) > 0
        assert len(parsed.edges) > 0

        # Verify Files detected
        file_ids = [n["id"] for n in parsed.nodes if n["type"] == "file"]
        assert any("models/base.py" in f for f in file_ids)
        assert any("models/user.py" in f for f in file_ids)
        assert any("services/auth.py" in f for f in file_ids)
        assert any("main.py" in f for f in file_ids)

        # Verify Classes detected
        classes = [n for n in parsed.nodes if n["type"] == "class"]
        class_names = [c["name"] for c in classes]
        assert "BaseModel" in class_names
        assert "User" in class_names
        assert "AuthService" in class_names

        # Verify Functions detected
        functions = [n for n in parsed.nodes if n["type"] in ("function", "method")]
        func_names = [f["name"] for f in functions]
        assert "to_dict" in func_names
        assert "authenticate" in func_names
        assert "start_app" in func_names

        # Verify Inheritance edge
        inheritance_edges = [e for e in parsed.edges if e["type"] == "inheritance"]
        assert len(inheritance_edges) >= 1

        # Verify Import edges
        import_edges = [e for e in parsed.edges if e["type"] == "import"]
        assert len(import_edges) >= 3
