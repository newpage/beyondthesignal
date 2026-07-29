from __future__ import annotations

import os
from typing import Any

import httpx
from fastapi import FastAPI
from pydantic import BaseModel, Field

PROJECT = "Beyond the Signal"
VERSION = "0.1.0"
OLLAMA_BASE_URL = os.getenv("OLLAMA_BASE_URL", "http://localhost:11434").rstrip("/")
OLLAMA_MODEL = os.getenv("OLLAMA_MODEL", "llama3.2:3b")

app = FastAPI(title=f"{PROJECT} AI Service", version=VERSION)


class ComponentStatus(BaseModel):
    name: str
    status: str
    details: dict[str, Any] = Field(default_factory=dict)


async def ollama_status() -> ComponentStatus:
    try:
        async with httpx.AsyncClient(timeout=2.5) as client:
            response = await client.get(f"{OLLAMA_BASE_URL}/api/tags")
            response.raise_for_status()
            models = [item.get("name") for item in response.json().get("models", [])]
            return ComponentStatus(
                name="ollama",
                status="UP",
                details={"baseUrl": OLLAMA_BASE_URL, "configuredModel": OLLAMA_MODEL, "models": models},
            )
    except Exception as exc:  # Health endpoints must report dependency failures, not crash.
        return ComponentStatus(
            name="ollama",
            status="DEGRADED",
            details={"baseUrl": OLLAMA_BASE_URL, "configuredModel": OLLAMA_MODEL, "error": str(exc)},
        )


@app.get("/health")
async def health() -> dict[str, Any]:
    ollama = await ollama_status()
    return {
        "service": "ai-service",
        "project": PROJECT,
        "version": VERSION,
        "status": "UP",
        "components": {"ollama": ollama.model_dump()},
    }


@app.get("/api/ai/status")
async def ai_status() -> dict[str, Any]:
    return await health()
