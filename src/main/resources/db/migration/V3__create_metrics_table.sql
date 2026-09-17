CREATE TABLE metrics (
    id UUID PRIMARY KEY,
    url_id UUID REFERENCES short_urls(id) ON DELETE CASCADE,
    clicked_at TIMESTAMP WITH TIME ZONE NOT NULL,
    operating_system VARCHAR(50),
    device_type VARCHAR(30),
    browser VARCHAR(50),
    country VARCHAR(100),
    state VARCHAR(100),
    referrer VARCHAR(255)
);