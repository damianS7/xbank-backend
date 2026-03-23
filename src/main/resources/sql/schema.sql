DROP SCHEMA IF EXISTS public CASCADE;

CREATE SCHEMA public AUTHORIZATION pg_database_owner;

COMMENT
ON SCHEMA public IS 'standard public schema';

-- User tables

CREATE TABLE public.user_accounts
(
    id            int4 GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email         VARCHAR(80) UNIQUE NOT NULL,
    password_hash VARCHAR(60)        NOT NULL,
    role          VARCHAR(30)        NOT NULL,
    status        VARCHAR(30)        NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TYPE public."user_token_type" AS ENUM (
	'ACCOUNT_VERIFICATION',
	'RESET_PASSWORD'
);
CREATE CAST (varchar as user_token_type) WITH INOUT AS IMPLICIT;

CREATE TABLE public.user_tokens
(
    id         int4 GENERATED ALWAYS AS IDENTITY NOT NULL,
    user_id    int4                              NOT NULL,
    token      varchar(100)                      NOT NULL,
    used       BOOLEAN   DEFAULT FALSE,
    type       VARCHAR(30)                       NOT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    expires_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    CONSTRAINT user_token_pkey PRIMARY KEY (id),
    CONSTRAINT user_token_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user_accounts (id) ON DELETE CASCADE
);
-- User profile

CREATE TABLE public.user_profiles
(
    id           int4 GENERATED ALWAYS AS IDENTITY NOT NULL,
    user_id      int4                              NOT NULL,
    first_name   varchar(20)                       NOT NULL,
    last_name    varchar(40)                       NOT NULL,
    phone_number varchar(14)                       NOT NULL,
    birthdate    date                              NOT NULL,
    gender       VARCHAR(30)                       NOT NULL,
    photo        varchar(100) NULL, -- path to image
    address      varchar(50)                       NOT NULL,
    postal_code  varchar(8)                        NOT NULL,
    country      varchar(12)                       NOT NULL,
    national_id  varchar(12)                       NOT NULL,
    updated_at   timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    CONSTRAINT user_profiles_pkey PRIMARY KEY (id),
    CONSTRAINT user_profiles_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user_accounts (id) ON DELETE CASCADE
);

CREATE CAST (varchar as notification_type) WITH INOUT AS IMPLICIT;

CREATE TABLE public.user_notifications
(
    id           int4 GENERATED ALWAYS AS IDENTITY NOT NULL,
    user_id      int4                              NOT NULL,
    type         VARCHAR(30)                       NOT NULL,
    metadata     jsonb                             NOT NULL,
    template_key VARCHAR                           NOT NULL,
    created_at   timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    CONSTRAINT user_notifications_pkey PRIMARY KEY (id),
    CONSTRAINT user_notifications_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user_accounts (id) ON DELETE CASCADE
);

CREATE TABLE public.user_settings
(
    id       int4 GENERATED ALWAYS AS IDENTITY NOT NULL,
    user_id  int4                              NOT NULL,
    settings jsonb                             NOT NULL,
    CONSTRAINT settings_pkey PRIMARY KEY (id),
    CONSTRAINT settings_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user_accounts (id) ON DELETE CASCADE
);

-- Banking Account

CREATE TABLE public.banking_accounts
(
    id               int4 GENERATED ALWAYS AS IDENTITY NOT NULL,
    user_id          int4                              NOT NULL,
    alias            varchar(64) NULL,
    account_number   varchar(32)                       NOT NULL,
    balance          numeric(15, 2) DEFAULT 0.00       NOT NULL,
    reserved_balance numeric(15, 2) DEFAULT 0.00       NOT NULL,
    account_type     VARCHAR(30)                       NOT NULL,
    account_currency VARCHAR(3)                        NOT NULL,
    account_status   VARCHAR(30)                       NOT NULL,
    notes            text NULL,
    created_at       timestamp      DEFAULT CURRENT_TIMESTAMP NULL,
    updated_at       timestamp      DEFAULT CURRENT_TIMESTAMP NULL,
    CONSTRAINT banking_accounts_account_number_key UNIQUE (account_number),
    CONSTRAINT banking_accounts_pkey PRIMARY KEY (id),
    CONSTRAINT banking_accounts_users_fk FOREIGN KEY (user_id) REFERENCES public.user_accounts (id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Banking Card

CREATE TABLE public.banking_cards
(
    id               int4 GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    account_id       int4                        NOT NULL,
    card_type        VARCHAR(30)                 NOT NULL,
    card_status      VARCHAR(30)                 NOT NULL,
    card_number      varchar(16)                 NOT NULL,
    card_pin         varchar(4)                  NOT NULL,
    card_cvv         varchar(3)                  NOT NULL,
    daily_limit      numeric(15, 2) DEFAULT 0.00 NOT NULL,
    expiration_year  INT                         NOT NULL,
    expiration_month INT                         NOT NULL,
    notes            text NULL,
    created_at       timestamp      DEFAULT CURRENT_TIMESTAMP NULL,
    updated_at       timestamp      DEFAULT CURRENT_TIMESTAMP NULL,
    CONSTRAINT banking_cards_card_number_key UNIQUE (card_number),
    CONSTRAINT fk_account_id_fkey FOREIGN KEY (account_id)
        REFERENCES public.banking_accounts (id)
        ON DELETE CASCADE
);

-- Banking Transfers

CREATE TABLE public.outgoing_transfers
(
    id                        int4 GENERATED ALWAYS AS IDENTITY NOT NULL,
    from_account_id           int4                              NOT NULL,
    to_account_id             int4 NULL,
    to_account_iban           VARCHAR(32)                       NOT NULL,
    provider_authorization_id VARCHAR(36) NULL,
    amount                    numeric(15, 2) DEFAULT 0.00       NOT NULL,
    type                      VARCHAR(30)                       NOT NULL,
    status                    VARCHAR(30)                       NOT NULL,
    description               text NULL,
    created_at                timestamp      DEFAULT CURRENT_TIMESTAMP NULL,
    updated_at                timestamp      DEFAULT CURRENT_TIMESTAMP NULL,
    CONSTRAINT outgoing_transfers_pkey PRIMARY KEY (id),
    CONSTRAINT outgoing_transfers_from_account_fk
        FOREIGN KEY (from_account_id)
            REFERENCES public.banking_accounts (id)
            ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT outgoing_transfers_to_account_fk
        FOREIGN KEY (to_account_id)
            REFERENCES public.banking_accounts (id)
            ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE public.incoming_transfers
(
    id                        int4 GENERATED ALWAYS AS IDENTITY NOT NULL,
    from_account_iban         VARCHAR(32)                       NOT NULL,
    to_account_id             int4                              NOT NULL,
    to_account_iban           VARCHAR(32)                       NOT NULL,
    provider_authorization_id VARCHAR(36)                       NOT NULL,
    amount                    numeric(15, 2) DEFAULT 0.00       NOT NULL,
    status                    VARCHAR(30)                       NOT NULL,
    reference                 text NULL,
    created_at                timestamp      DEFAULT CURRENT_TIMESTAMP NULL,
    updated_at                timestamp      DEFAULT CURRENT_TIMESTAMP NULL,
    CONSTRAINT incoming_transfers_pkey PRIMARY KEY (id),
    CONSTRAINT incoming_transfers_to_account_fk
        FOREIGN KEY (to_account_id)
            REFERENCES public.banking_accounts (id)
            ON DELETE CASCADE ON UPDATE CASCADE
);

-- Banking transactions

CREATE TABLE public.banking_transactions
(
    id                   int4 GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    account_id           int4           NOT NULL,
    outgoing_transfer_id int4 NULL,
    incoming_transfer_id int4 NULL,
    card_id              int4 NULL,
    authorization_id     VARCHAR(36)    NOT NULL,
    balance_before       numeric(15, 2) NOT NULL,
    balance_after        numeric(15, 2) NOT NULL,
    transaction_type     VARCHAR(30)    NOT NULL,
    amount               numeric(15, 2) NOT NULL,
    description          text NULL,
    status               VARCHAR(30)    NOT NULL,
    created_at           timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    updated_at           timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    CONSTRAINT fk_transactions_account FOREIGN KEY (account_id)
        REFERENCES public.banking_accounts (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_transactions_card FOREIGN KEY (card_id)
        REFERENCES public.banking_cards (id)
        ON DELETE SET NULL,
    CONSTRAINT fk_transactions_outgoing_transfer FOREIGN KEY (outgoing_transfer_id)
        REFERENCES public.outgoing_transfers (id)
        ON DELETE SET NULL,
    CONSTRAINT fk_transactions_incoming_transfer FOREIGN KEY (incoming_transfer_id)
        REFERENCES public.incoming_transfers (id)
        ON DELETE SET NULL
);

CREATE TABLE public.merchants
(
    id            int4 GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id       int4         NOT NULL UNIQUE,
    merchant_name VARCHAR(255) NOT NULL,
    callback_url  VARCHAR(255) NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_merchant_user_account
        FOREIGN KEY (user_id)
            REFERENCES public.user_accounts (id)
            ON DELETE CASCADE
);

CREATE TABLE public.payment_intents
(
    id                    int4 GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    merchant_id           int4           NOT NULL,
    order_id              varchar(30)    NOT NULL,
    status                VARCHAR(30)    NOT NULL,
    amount                numeric(15, 2) NOT NULL,
    currency              VARCHAR(3)     NOT NULL,
    description           VARCHAR(60)    NOT NULL,
    created_at            timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    updated_at            timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    CONSTRAINT fk_merchants FOREIGN KEY (merchant_id)
        REFERENCES public.merchants (id)
        ON DELETE RESTRICT
);