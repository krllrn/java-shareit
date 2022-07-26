DROP TABLE IF EXISTS requests, bookings, items, comments, users;

CREATE TABLE IF NOT EXISTS users (
	id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
	email VARCHAR(128) UNIQUE,
	name VARCHAR(128),
	CONSTRAINT pk_user PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS requests (
	id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
	request_owner_id BIGINT REFERENCES users (id) ON DELETE CASCADE,
	description VARCHAR(512),
	created TIMESTAMP WITH TIME ZONE,
	CONSTRAINT pk_request PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS items (
	id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
	owner_id BIGINT REFERENCES users (id) ON DELETE CASCADE,
	name VARCHAR(128),
	description VARCHAR(512),
	available BOOLEAN,
	comment_id BIGINT,
	request_id BIGINT REFERENCES requests (id) ON DELETE SET NULL,
	CONSTRAINT pk_item PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS comments (
	id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
	item_id BIGINT REFERENCES items (id) ON DELETE CASCADE,
	text TEXT,
	author_name VARCHAR(128),
	created TIMESTAMP WITH TIME ZONE,
	CONSTRAINT pk_comment PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS bookings (
	id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
	item_id BIGINT REFERENCES items (id) ON DELETE CASCADE,
	start_date TIMESTAMP WITH TIME ZONE,
	end_date TIMESTAMP WITH TIME ZONE,
	booker_id BIGINT REFERENCES users (id),
	status VARCHAR(20),
	item_owner_id BIGINT,
	CONSTRAINT pk_booking PRIMARY KEY (id)
);



