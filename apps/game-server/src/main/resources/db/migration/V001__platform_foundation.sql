CREATE TABLE platform_component (
    component_code VARCHAR(64) PRIMARY KEY,
    component_name VARCHAR(128) NOT NULL,
    installed_version VARCHAR(32) NOT NULL,
    installed_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO platform_component(component_code, component_name, installed_version)
VALUES ('platform-foundation', 'Beyond the Signal Platform Foundation', '0.1.0');
