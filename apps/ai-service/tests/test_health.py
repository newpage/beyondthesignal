from fastapi.testclient import TestClient
from app.main import app


def test_health_service_remains_available_when_optional_ollama_is_absent():
    response = TestClient(app).get("/health")
    assert response.status_code == 200
    body = response.json()
    assert body["project"] == "Beyond the Signal"
    assert body["status"] == "UP"
    assert body["components"]["ollama"]["status"] in {"UP", "DEGRADED"}
