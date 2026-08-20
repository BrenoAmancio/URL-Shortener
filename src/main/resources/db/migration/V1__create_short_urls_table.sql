CREATE TABLE short_urls (
                            id UUID PRIMARY KEY,
                            code VARCHAR(10) NOT NULL UNIQUE,
                            original_url TEXT NOT NULL,
                            created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                            expires_at TIMESTAMP WITH TIME ZONE
);