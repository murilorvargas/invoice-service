CREATE DATABASE IF NOT EXISTS invoice_db;

CREATE TABLE invoice_db.wallet_status (
    id          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    enumerator  VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at  TIMESTAMP(3) NULL     DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3)
);

INSERT INTO invoice_db.wallet_status(enumerator) VALUES ('ACTIVE');

CREATE TABLE invoice_db.card_status (
    id          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    enumerator  VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at  TIMESTAMP(3) NULL     DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3)
);

INSERT INTO invoice_db.card_status(enumerator) VALUES ('ACTIVE');

CREATE TABLE invoice_db.card_entry_status (
    id          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    enumerator  VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at  TIMESTAMP(3) NULL     DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3)
);

INSERT INTO invoice_db.card_entry_status(enumerator) VALUES ('PROCESSING_CONCLUSION');
INSERT INTO invoice_db.card_entry_status(enumerator) VALUES ('CONCLUDED');
INSERT INTO invoice_db.card_entry_status(enumerator) VALUES ('PROCESSING_CANCELLATION');
INSERT INTO invoice_db.card_entry_status(enumerator) VALUES ('CANCELLED');

CREATE TABLE invoice_db.card_entry_type (
    id          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    enumerator  VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at  TIMESTAMP(3) NULL     DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3)
);

INSERT INTO invoice_db.card_entry_type(enumerator) VALUES ('PURCHASE');
INSERT INTO invoice_db.card_entry_type(enumerator) VALUES ('WITHDRAW');

CREATE TABLE invoice_db.invoice_status (
    id          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    enumerator  VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at  TIMESTAMP(3) NULL     DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3)
);

INSERT INTO invoice_db.invoice_status(enumerator) VALUES ('OPENED');
INSERT INTO invoice_db.invoice_status(enumerator) VALUES ('CONCLUDED');
INSERT INTO invoice_db.invoice_status(enumerator) VALUES ('CANCELLED');

CREATE TABLE invoice_db.invoice_item_status (
    id          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    enumerator  VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at  TIMESTAMP(3) NULL     DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3)
);

INSERT INTO invoice_db.invoice_item_status(enumerator) VALUES ('CONCLUDED');
INSERT INTO invoice_db.invoice_item_status(enumerator) VALUES ('CANCELLED');

CREATE TABLE invoice_db.due_type (
    id          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    enumerator  VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at  TIMESTAMP(3) NULL     DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3)
);

INSERT INTO invoice_db.due_type(enumerator) VALUES ('FIXED_DAY');
INSERT INTO invoice_db.due_type(enumerator) VALUES ('RULE');

CREATE TABLE invoice_db.invoice_configuration (
    id                              BIGINT         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    version                         BIGINT         NULL,
    invoice_configuration_key       VARCHAR(36)    NOT NULL,
    closing_fixed_day               INT            NOT NULL,
    due_fixed_day                   INT            NULL,
    due_offset_months               INT            NULL,
    due_days_after_closing          INT            NULL,
    fine_percentage                 DECIMAL(5,4)   NOT NULL,
    interest_percentage             DECIMAL(5,4)   NOT NULL,
    revolving_interest_percentage   DECIMAL(5,4)   NOT NULL,
    due_type_id                     BIGINT         NOT NULL,
    created_at                      TIMESTAMP(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at                      TIMESTAMP(3)   NULL     DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    CONSTRAINT invoice_configuration_key_unique UNIQUE (invoice_configuration_key),
    CONSTRAINT fk1_due_type FOREIGN KEY (due_type_id) REFERENCES invoice_db.due_type(id)
);

CREATE TABLE invoice_db.wallet (
    id                          BIGINT         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    version                     BIGINT         NULL,
    wallet_key                  VARCHAR(36)    NOT NULL,
    requester_key               VARCHAR(36)    NOT NULL,
    request_control_key         VARCHAR(36)    NOT NULL,
    document_number             VARCHAR(14)    NOT NULL,
    invoice_configuration_id    BIGINT         NOT NULL,
    wallet_status_id            BIGINT         NOT NULL,
    created_at                  TIMESTAMP(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at                  TIMESTAMP(3)   NULL     DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    CONSTRAINT wallet_key_unique UNIQUE (wallet_key),
    CONSTRAINT wallet_requester_request_control_key_unique UNIQUE (requester_key, request_control_key),
    CONSTRAINT wallet_requester_document_number_unique UNIQUE (requester_key, document_number),
    CONSTRAINT fk1_invoice_configuration FOREIGN KEY (invoice_configuration_id) REFERENCES invoice_db.invoice_configuration(id),
    CONSTRAINT fk1_wallet_status FOREIGN KEY (wallet_status_id) REFERENCES invoice_db.wallet_status(id)
);

CREATE TABLE invoice_db.wallet_limit (
    id                  BIGINT         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    version             BIGINT         NULL,
    wallet_limit_key    VARCHAR(36)    NOT NULL,
    limit_amount        DECIMAL(19,2)  NOT NULL,
    used_limit_amount   DECIMAL(19,2)  NOT NULL,
    wallet_id           BIGINT         NOT NULL,
    created_at          TIMESTAMP(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at          TIMESTAMP(3)   NULL     DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    CONSTRAINT wallet_limit_key_unique UNIQUE (wallet_limit_key),
    CONSTRAINT fk1_wallet FOREIGN KEY (wallet_id) REFERENCES invoice_db.wallet(id)
);

CREATE TABLE invoice_db.card (
    id                      BIGINT         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    version                 BIGINT         NULL,
    card_key                VARCHAR(36)    NOT NULL,
    request_control_key     VARCHAR(36)    NOT NULL,
    document_number         VARCHAR(14)    NOT NULL,
    limit_amount            DECIMAL(19,2)  NOT NULL,
    used_limit_amount       DECIMAL(19,2)  NOT NULL,
    wallet_id               BIGINT         NOT NULL,
    card_status_id          BIGINT         NOT NULL,
    created_at              TIMESTAMP(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at              TIMESTAMP(3)   NULL     DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    CONSTRAINT card_key_unique UNIQUE (card_key),
    CONSTRAINT card_wallet_request_control_key_unique UNIQUE (wallet_id, request_control_key),
    CONSTRAINT fk2_wallet FOREIGN KEY (wallet_id) REFERENCES invoice_db.wallet(id),
    CONSTRAINT fk1_card_status FOREIGN KEY (card_status_id) REFERENCES invoice_db.card_status(id)
);

CREATE INDEX card_document_number_index ON invoice_db.card (document_number);

CREATE TABLE invoice_db.card_entry (
    id                      BIGINT         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    version                 BIGINT         NULL,
    card_entry_key          VARCHAR(36)    NOT NULL,
    request_control_key     VARCHAR(36)    NOT NULL,
    amount                  DECIMAL(19,2)  NOT NULL,
    number_of_installments  INT            NOT NULL,
    card_entry_data         JSON           NOT NULL,
    card_id                 BIGINT         NOT NULL,
    card_entry_type_id      BIGINT         NOT NULL,
    card_entry_status_id    BIGINT         NOT NULL,
    created_at              TIMESTAMP(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at              TIMESTAMP(3)   NULL     DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    CONSTRAINT card_entry_key_unique UNIQUE (card_entry_key),
    CONSTRAINT card_entry_request_control_key_unique UNIQUE (request_control_key),
    CONSTRAINT fk1_card FOREIGN KEY (card_id) REFERENCES invoice_db.card(id),
    CONSTRAINT fk1_card_entry_type FOREIGN KEY (card_entry_type_id) REFERENCES invoice_db.card_entry_type(id),
    CONSTRAINT fk1_card_entry_status FOREIGN KEY (card_entry_status_id) REFERENCES invoice_db.card_entry_status(id)
);

CREATE TABLE invoice_db.invoice (
    id                  BIGINT         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    version             BIGINT         NULL,
    invoice_key         VARCHAR(36)    NOT NULL,
    closing_date        DATE           NOT NULL,
    due_date            DATE           NOT NULL,
    amount              DECIMAL(19,2)  NOT NULL,
    wallet_id           BIGINT         NOT NULL,
    invoice_status_id   BIGINT         NOT NULL,
    created_at          TIMESTAMP(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at          TIMESTAMP(3)   NULL     DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    CONSTRAINT invoice_key_unique UNIQUE (invoice_key),
    CONSTRAINT fk3_wallet FOREIGN KEY (wallet_id) REFERENCES invoice_db.wallet(id),
    CONSTRAINT fk1_invoice_status FOREIGN KEY (invoice_status_id) REFERENCES invoice_db.invoice_status(id)
);

CREATE INDEX invoice_wallet_closing_date_index ON invoice_db.invoice (wallet_id, closing_date);

CREATE TABLE invoice_db.invoice_item (
    id                      BIGINT         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    version                 BIGINT         NULL,
    invoice_item_key        VARCHAR(36)    NOT NULL,
    amount                  DECIMAL(19,2)  NOT NULL,
    description             VARCHAR(255)   NOT NULL,
    invoice_id              BIGINT         NOT NULL,
    invoice_item_status_id  BIGINT         NOT NULL,
    created_at              TIMESTAMP(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at              TIMESTAMP(3)   NULL     DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    CONSTRAINT invoice_item_key_unique UNIQUE (invoice_item_key),
    CONSTRAINT fk1_invoice FOREIGN KEY (invoice_id) REFERENCES invoice_db.invoice(id),
    CONSTRAINT fk1_invoice_item_status FOREIGN KEY (invoice_item_status_id) REFERENCES invoice_db.invoice_item_status(id)
);