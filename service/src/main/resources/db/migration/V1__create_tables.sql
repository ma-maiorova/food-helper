CREATE TABLE delivery_service (
                                  id              BIGSERIAL PRIMARY KEY,
                                  code            VARCHAR(64) NOT NULL UNIQUE,
                                  name            VARCHAR(255) NOT NULL,
                                  site_url        VARCHAR(512),
                                  logo_url        VARCHAR(512),
                                  active          BOOLEAN NOT NULL DEFAULT TRUE,
                                  created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                  updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE product (
                         id                  BIGSERIAL PRIMARY KEY,
                         name                VARCHAR(512) NOT NULL,
                         url                 VARCHAR(1024) NOT NULL,
                         price               INTEGER NOT NULL,
                         currency            VARCHAR(8) NOT NULL DEFAULT 'RUB',
                         delivery_service_id BIGINT NOT NULL REFERENCES delivery_service (id),
                         created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                         updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE product_variant (
                                 id           BIGSERIAL PRIMARY KEY,
                                 product_id   BIGINT NOT NULL REFERENCES product (id) ON DELETE CASCADE,
                                 manufacturer VARCHAR(255),
                                 composition  TEXT,
                                 weight       INTEGER,
                                 calories     INTEGER,
                                 protein      DOUBLE PRECISION,
                                 fat          DOUBLE PRECISION,
                                 carbs        DOUBLE PRECISION
);

CREATE INDEX idx_product_delivery_service ON product (delivery_service_id);
CREATE INDEX idx_product_name ON product (name);

CREATE INDEX idx_product_variant_calories ON product_variant (calories);
CREATE INDEX idx_product_variant_protein  ON product_variant (protein);
CREATE INDEX idx_product_variant_fat      ON product_variant (fat);
CREATE INDEX idx_product_variant_carbs    ON product_variant (carbs);
