import io
import os
import re
import shutil
import subprocess
import tempfile
import urllib.request
import zipfile
from pathlib import Path
from typing import Optional, Tuple
from app.core.config import settings

IGNORED_DIRS = {
    ".git",
    "node_modules",
    ".venv",
    "venv",
    "env",
    "__pycache__",
    "dist",
    "build",
    ".idea",
    ".vscode",
    ".pytest_cache",
    ".mypy_cache",
    ".ruff_cache",
    "eggs",
    ".eggs",
    "site-packages",
}

class IngestionService:
    def __init__(self):
        settings.storage_dir.mkdir(parents=True, exist_ok=True)

    def get_project_storage_dir(self, project_id: str) -> Path:
        p = (settings.storage_dir / project_id).resolve()
        p.mkdir(parents=True, exist_ok=True)
        return p

    def ingest_zip_bytes(self, project_id: str, zip_bytes: bytes) -> str:
        """
        Safely extracts ZIP file bytes to the project's storage directory.
        Includes path traversal (Zip Slip) protection and directory filtering.
        """
        target_dir = self.get_project_storage_dir(project_id)
        if target_dir.exists():
            for item in target_dir.iterdir():
                if item.is_dir():
                    shutil.rmtree(item)
                else:
                    item.unlink()

        with zipfile.ZipFile(io.BytesIO(zip_bytes)) as zf:
            for member in zf.infolist():
                filename = member.filename
                parts = Path(filename).parts
                if any(ignored in parts for ignored in IGNORED_DIRS):
                    continue

                # Protect against Zip Slip
                target_path = (target_dir / filename).resolve()
                if not str(target_path).startswith(str(target_dir)):
                    raise ValueError(f"Security error: Path traversal detected in ZIP entry: {filename}")

                if member.is_dir():
                    target_path.mkdir(parents=True, exist_ok=True)
                else:
                    target_path.parent.mkdir(parents=True, exist_ok=True)
                    with zf.open(member) as source, open(target_path, "wb") as dest:
                        shutil.copyfileobj(source, dest)

        # If the ZIP extracted a GitHub archive wrapper folder (e.g. repo-main or repo-master), unwrap it
        extracted_items = [p for p in target_dir.iterdir() if not p.name.startswith(".")]
        if len(extracted_items) == 1 and extracted_items[0].is_dir():
            dir_name = extracted_items[0].name.lower()
            # Match GitHub archive naming: repo-main, repo-master, or owner-repo-commithash
            if re.match(r'^.+-(main|master)$', dir_name) or re.match(r'^.+-[a-f0-9]{7,40}$', dir_name):
                return str(extracted_items[0].resolve())

        return str(target_dir.resolve())

    def ingest_github_repo(self, project_id: str, repo_url: str) -> str:
        """
        Safely downloads and extracts a public GitHub repository.
        Never executes any repository code.
        """
        match = re.match(r"^https?://(?:www\.)?github\.com/([A-Za-z0-9_.-]+)/([A-Za-z0-9_.-]+?)(?:\.git)?(?:/.*)?$", repo_url.strip())
        if not match:
            raise ValueError(f"Invalid GitHub URL: '{repo_url}'. Expected format: https://github.com/owner/repo")

        owner = match.group(1)
        repo = match.group(2)
        if repo.endswith(".git"):
            repo = repo[:-4]

        branches = ["main", "master"]
        zip_bytes = None
        last_error = None

        headers = {
            "User-Agent": "DevTwin-Architecture-Analyzer/1.0",
            "Accept": "application/vnd.github.v3+json",
        }

        # 1. Try public codeload zip archive with 30s timeout
        for branch in branches:
            download_url = f"https://codeload.github.com/{owner}/{repo}/zip/refs/heads/{branch}"
            try:
                req = urllib.request.Request(download_url, headers=headers)
                with urllib.request.urlopen(req, timeout=30) as response:
                    if response.status == 200:
                        zip_bytes = response.read()
                        break
            except Exception as e:
                last_error = e

        # 2. Try github api zipball
        if not zip_bytes:
            try:
                download_url = f"https://api.github.com/repos/{owner}/{repo}/zipball"
                req = urllib.request.Request(download_url, headers=headers)
                with urllib.request.urlopen(req, timeout=30) as response:
                    if response.status == 200:
                        zip_bytes = response.read()
            except Exception as e:
                last_error = e

        # 3. If zip download worked, extract it
        if zip_bytes:
            return self.ingest_zip_bytes(project_id, zip_bytes)

        # 4. Fallback: try git clone --depth 1 with 60s timeout
        target_dir = self.get_project_storage_dir(project_id)
        if target_dir.exists():
            for item in target_dir.iterdir():
                if item.is_dir():
                    shutil.rmtree(item)
                else:
                    item.unlink()

        try:
            clone_cmd = ["git", "clone", "--depth", "1", f"https://github.com/{owner}/{repo}.git", str(target_dir)]
            res = subprocess.run(clone_cmd, capture_output=True, text=True, timeout=60)
            if res.returncode == 0:
                # Remove .git folder for safety
                git_folder = target_dir / ".git"
                if git_folder.exists():
                    shutil.rmtree(git_folder, ignore_errors=True)
                return str(target_dir.resolve())
        except Exception as e:
            last_error = e

        raise ValueError(
            f"Could not connect to GitHub to download repository '{owner}/{repo}' (network timeout/SSL error). "
            f"Please download the repository directly and upload it using the 'Upload Project ZIP' tab."
        )

ingestion_service = IngestionService()
