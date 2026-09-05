import json
import os
import sqlite3
from pathlib import Path
from typing import Any, Dict, List, Optional
from app.core.config import settings

class Database:
    """
    Unified database manager supporting both PostgreSQL and SQLite persistence.
    Provides clean SQL execution and table initialization for DevTwin.
    """
    def __init__(self, db_url: Optional[str] = None):
        self.db_url = db_url or settings.database_url
        self.is_postgres = self.db_url.startswith("postgres://") or self.db_url.startswith("postgresql://")
        self._init_db()

    def _get_connection(self):
        if self.is_postgres:
            import psycopg2
            import psycopg2.extras
            # Handle Render postgresql:// vs postgres:// format
            url = self.db_url.replace("postgres://", "postgresql://", 1)
            conn = psycopg2.connect(url)
            conn.autocommit = True
            return conn
        else:
            # SQLite path
            db_path = self.db_url.replace("sqlite:///", "").replace("sqlite://", "")
            if db_path != ":memory:":
                Path(db_path).parent.mkdir(parents=True, exist_ok=True)
            conn = sqlite3.connect(db_path, check_same_thread=False)
            conn.row_factory = sqlite3.Row
            return conn

    def _init_db(self):
        try:
            with self._get_connection() as conn:
                cursor = conn.cursor()
                if self.is_postgres:
                    cursor.execute("""
                        CREATE TABLE IF NOT EXISTS projects (
                            id VARCHAR(64) PRIMARY KEY,
                            name VARCHAR(255) NOT NULL,
                            repository_url TEXT,
                            source_path TEXT,
                            status VARCHAR(64) NOT NULL DEFAULT 'created',
                            graph_data JSONB DEFAULT '{"nodes": [], "edges": []}',
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                        );
                        CREATE TABLE IF NOT EXISTS analyses (
                            id VARCHAR(64) PRIMARY KEY,
                            project_id VARCHAR(64) REFERENCES projects(id) ON DELETE CASCADE,
                            status VARCHAR(64) NOT NULL,
                            summary_data JSONB,
                            metrics_data JSONB,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                        );
                    """)
                else:
                    cursor.execute("""
                        CREATE TABLE IF NOT EXISTS projects (
                            id TEXT PRIMARY KEY,
                            name TEXT NOT NULL,
                            repository_url TEXT,
                            source_path TEXT,
                            status TEXT NOT NULL DEFAULT 'created',
                            graph_data TEXT DEFAULT '{"nodes": [], "edges": []}',
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                        );
                    """)
                    cursor.execute("""
                        CREATE TABLE IF NOT EXISTS analyses (
                            id TEXT PRIMARY KEY,
                            project_id TEXT REFERENCES projects(id) ON DELETE CASCADE,
                            status TEXT NOT NULL,
                            summary_data TEXT,
                            metrics_data TEXT,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                        );
                    """)
                    conn.commit()
        except Exception as e:
            # In case of DB connection error on boot, log and continue
            print(f"[Database Warning] Failed to initialize DB: {e}")

    def execute(self, query: str, params: tuple = ()):
        with self._get_connection() as conn:
            cursor = conn.cursor()
            # Replace ? with %s if postgres
            if self.is_postgres:
                query = query.replace("?", "%s")
            cursor.execute(query, params)
            if not self.is_postgres:
                conn.commit()
            return cursor

    def fetchone(self, query: str, params: tuple = ()) -> Optional[Dict[str, Any]]:
        with self._get_connection() as conn:
            cursor = conn.cursor()
            if self.is_postgres:
                import psycopg2.extras
                cursor = conn.cursor(cursor_factory=psycopg2.extras.RealDictCursor)
                query = query.replace("?", "%s")
            cursor.execute(query, params)
            row = cursor.fetchone()
            if row is None:
                return None
            if self.is_postgres:
                return dict(row)
            return dict(row)

    def fetchall(self, query: str, params: tuple = ()) -> List[Dict[str, Any]]:
        with self._get_connection() as conn:
            cursor = conn.cursor()
            if self.is_postgres:
                import psycopg2.extras
                cursor = conn.cursor(cursor_factory=psycopg2.extras.RealDictCursor)
                query = query.replace("?", "%s")
            cursor.execute(query, params)
            rows = cursor.fetchall()
            if self.is_postgres:
                return [dict(r) for r in rows]
            return [dict(r) for r in rows]

db = Database()
