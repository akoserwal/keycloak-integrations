-- serviceaccounts definition
CREATE TABLE public.serviceaccounts (
	client_id varchar(255) NOT NULL,
	operation varchar(50) NULL,
	count int8 NULL DEFAULT 0,
	ip varchar(255) NULL,
	status varchar(255) NULL,
	created_at timestamp NULL DEFAULT LOCALTIMESTAMP,
	last_update_at timestamp NULL DEFAULT LOCALTIMESTAMP,
	CONSTRAINT serviceaccounts_pkey PRIMARY KEY (client_id)
);


CREATE TABLE serviceaccounts_logins (
	client_id varchar(80) NOT NULL,
	login_timestamp timestamp NULL DEFAULT LOCALTIMESTAMP
);
-- Drop table

-- DROP TABLE serviceaccounts;
-- DROP TABLE serviceaccounts_logins;