DROP SCHEMA IF EXISTS public CASCADE;

CREATE SCHEMA public AUTHORIZATION pg_database_owner;

COMMENT ON SCHEMA public IS 'standard public schema';

-- User tables

CREATE TYPE public."user_status_type" AS ENUM (
	'PENDING_VERIFICATION',
	'VERIFIED',
	'SUSPENDED'
);
CREATE CAST (varchar as user_status_type) WITH INOUT AS IMPLICIT;

CREATE TYPE public."user_role_type" AS ENUM (
	'CUSTOMER',
	'ADMIN'
);
CREATE CAST (varchar as user_role_type) WITH INOUT AS IMPLICIT;

CREATE TABLE public.user_accounts (
  id int4 GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  email VARCHAR(80) UNIQUE NOT NULL,
  password_hash VARCHAR(60) NOT NULL,
  role public.user_role_type DEFAULT 'CUSTOMER'::user_role_type NOT NULL,
  status public.user_status_type DEFAULT 'PENDING_VERIFICATION'::user_status_type NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TYPE public."user_token_type" AS ENUM (
	'ACCOUNT_VERIFICATION',
	'RESET_PASSWORD'
);
CREATE CAST (varchar as user_token_type) WITH INOUT AS IMPLICIT;

CREATE TABLE public.user_tokens (
	id int4 GENERATED ALWAYS AS IDENTITY NOT NULL,
	user_id int4 NOT NULL,
	token varchar(100) NOT NULL,
	used BOOLEAN DEFAULT FALSE,
	type public."user_token_type" NOT NULL,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	expires_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	CONSTRAINT user_token_pkey PRIMARY KEY (id),
	CONSTRAINT user_token_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user_accounts(id) ON DELETE CASCADE
);
-- User profile

CREATE TYPE public."user_gender_type" AS ENUM (
	'MALE',
	'FEMALE'
);
CREATE CAST (varchar as user_gender_type) WITH INOUT AS IMPLICIT;

CREATE TABLE public.user_profiles (
	id int4 GENERATED ALWAYS AS IDENTITY NOT NULL,
	user_id int4 NOT NULL,
	first_name varchar(20) NOT NULL,
	last_name varchar(40) NOT NULL,
	phone_number varchar(14) NOT NULL,
	birthdate date NOT NULL,
	gender public."user_gender_type" NOT NULL,
	photo varchar(100) NULL, -- path to image
	address varchar(50) NOT NULL,
	postal_code varchar(8) NOT NULL,
	country varchar(12) NOT NULL,
	national_id varchar(12) NOT NULL,
	updated_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	CONSTRAINT user_profiles_pkey PRIMARY KEY (id),
	CONSTRAINT user_profiles_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user_accounts(id) ON DELETE CASCADE
);

CREATE TYPE public."notification_type" AS ENUM (
    'TRANSACTION',
    'TRANSFER',
    'CARD',
    'ACCOUNT',
    'SECURITY',
    'SYSTEM'
);
CREATE CAST (varchar as notification_type) WITH INOUT AS IMPLICIT;

CREATE TABLE public.user_notifications (
	id int4 GENERATED ALWAYS AS IDENTITY NOT NULL,
	user_id int4 NOT NULL,
	type public."notification_type" NOT NULL,
	metadata jsonb NOT NULL,
	template_key VARCHAR NOT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	CONSTRAINT user_notifications_pkey PRIMARY KEY (id),
    CONSTRAINT user_notifications_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user_accounts(id) ON DELETE CASCADE
);

CREATE TABLE public.user_settings (
	id int4 GENERATED ALWAYS AS IDENTITY NOT NULL,
	user_id int4 NOT NULL,
	settings jsonb NOT NULL,
	CONSTRAINT settings_pkey PRIMARY KEY (id),
    CONSTRAINT settings_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user_accounts(id) ON DELETE CASCADE
);

-- Banking Account

CREATE TYPE public."banking_account_currency_type" AS ENUM (
	'EUR',
	'USD'
);
CREATE CAST (varchar as banking_account_currency_type) WITH INOUT AS IMPLICIT;

CREATE TYPE public."banking_account_status_type" AS ENUM (
  'PENDING_ACTIVATION',
  'ACTIVE',
  'SUSPENDED',
  'CLOSED'
);
CREATE CAST (varchar as banking_account_status_type) WITH INOUT AS IMPLICIT;

CREATE TYPE public."banking_account_type" AS ENUM (
	'SAVINGS',
	'CHECKING'
);
CREATE CAST (varchar as banking_account_type) WITH INOUT AS IMPLICIT;

CREATE TABLE public.banking_accounts (
	id int4 GENERATED ALWAYS AS IDENTITY NOT NULL,
	user_id int4 NOT NULL,
	alias varchar(64) NULL,
	account_number varchar(32) NOT NULL,
	balance numeric(15, 2) DEFAULT 0.00 NOT NULL,
	account_type public."banking_account_type" DEFAULT 'SAVINGS'::banking_account_type NOT NULL,
	account_currency public."banking_account_currency_type" DEFAULT 'EUR'::banking_account_currency_type NOT NULL,
	account_status public."banking_account_status_type" DEFAULT 'PENDING_ACTIVATION'::banking_account_status_type NOT NULL,
	notes text NULL,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	updated_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	CONSTRAINT banking_accounts_account_number_key UNIQUE (account_number),
	CONSTRAINT banking_accounts_pkey PRIMARY KEY (id),
	CONSTRAINT banking_accounts_users_fk FOREIGN KEY (user_id) REFERENCES public.user_accounts(id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Banking Card

CREATE TYPE public."banking_card_status_type" AS ENUM (
  'ACTIVE',      -- Actiaved and working
  'LOCKED',    -- Deactivated by user or bank (can be activated again)
  'DISABLED',   -- Disabled by admin (lost, account closed, robbed)
  'EXPIRED',     -- Card expired.
  'PENDING_ACTIVATION' -- Emitted but not activated by user
);
CREATE CAST (varchar as banking_card_status_type) WITH INOUT AS IMPLICIT;

CREATE TYPE public."banking_card_type" AS ENUM (
	'CREDIT',
	'DEBIT'
);
CREATE CAST (varchar as banking_card_type) WITH INOUT AS IMPLICIT;

CREATE TABLE public.banking_cards (
	id int4 GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	account_id int4 NOT NULL,
	card_type public."banking_card_type" NOT NULL,
	card_status public."banking_card_status_type" DEFAULT 'PENDING_ACTIVATION'::banking_card_status_type NOT NULL,
	card_number varchar(32) NOT NULL,
	card_pin varchar(4) NOT NULL,
	card_cvv varchar(3) NOT NULL,
	daily_limit numeric(15, 2) DEFAULT 0.00 NOT NULL,
	expired_date date NOT NULL,
	notes text NULL,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	updated_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	CONSTRAINT banking_cards_card_number_key UNIQUE (card_number),
	CONSTRAINT fk_account_id_fkey FOREIGN KEY (account_id)
	    REFERENCES public.banking_accounts(id)
	    ON DELETE CASCADE
);

-- Banking transactions

CREATE TYPE public."banking_transaction_status_type" AS ENUM (
	'PENDING',
	'FAILED',
	'COMPLETED',
	'REJECTED'
);
CREATE CAST (varchar as banking_transaction_status_type) WITH INOUT AS IMPLICIT;

CREATE TYPE public."banking_transaction_type" AS ENUM (
	'DEPOSIT',
	'WITHDRAWAL',
	'CARD_CHARGE',
	'TRANSFER_TO',
	'TRANSFER_FROM'
);
CREATE CAST (varchar as banking_transaction_type) WITH INOUT AS IMPLICIT;

CREATE TABLE public.banking_transactions (
	id int4 GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	account_id int4 NOT NULL,
	transfer_id int4 NULL,
	card_id int4 NULL,
	balance_before numeric(15, 2) NOT NULL,
	balance_after numeric(15, 2) NOT NULL,
	transaction_type public."banking_transaction_type" NOT NULL,
	amount numeric(15, 2) NOT NULL,
	description text NULL,
	status public."banking_transaction_status_type" DEFAULT 'PENDING'::banking_transaction_status_type NOT NULL,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	updated_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	CONSTRAINT fk_transactions_account FOREIGN KEY (account_id)
        REFERENCES public.banking_accounts(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_transactions_card FOREIGN KEY (card_id)
        REFERENCES public.banking_cards(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_transactions_transfer FOREIGN KEY (transfer_id)
        REFERENCES public.banking_transfers(id)
        ON DELETE SET NULL
);

-- Banking Transfers

CREATE TYPE public."banking_transfer_status_type" AS ENUM (
  'PENDING',
  'REJECTED',
  'CONFIRMED'
);
CREATE CAST (varchar as banking_transfer_status_type) WITH INOUT AS IMPLICIT;

CREATE TABLE public.banking_transfers (
    id int4 GENERATED ALWAYS AS IDENTITY NOT NULL,
    from_account_id int4 NOT NULL,
    to_account_id int4 NOT NULL,
    amount numeric(15, 2) DEFAULT 0.00 NOT NULL,
    status public."banking_transfer_status_type" DEFAULT 'PENDING'::banking_transfer_status_type NOT NULL,
    description text NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    updated_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    CONSTRAINT banking_transfers_pkey PRIMARY KEY (id),
    CONSTRAINT banking_transfers_from_account_fk
        FOREIGN KEY (from_account_id)
        REFERENCES public.banking_accounts(id)
            ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT banking_transfers_to_account_fk
        FOREIGN KEY (to_account_id)
        REFERENCES public.banking_accounts(id)
        ON DELETE CASCADE ON UPDATE CASCADE
);
