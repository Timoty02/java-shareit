DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS requests;
DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(512) NOT NULL
);

CREATE TABLE IF NOT EXISTS requests (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
  description VARCHAR(512) NOT NULL,
  requester_id BIGINT NOT NULL,
  created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  CONSTRAINT "requester_id" FOREIGN KEY (requester_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS items (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(512) NOT NULL,
  is_available BOOLEAN NOT NULL,
  owner_id BIGINT NOT NULL,
  request_id BIGINT,
  CONSTRAINT "owner_id" FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT "request_id" FOREIGN KEY (request_id) REFERENCES requests (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
  start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  item_id BIGINT NOT NULL,
  booker_id BIGINT NOT NULL,
  status VARCHAR(255) NOT NULL,
  CONSTRAINT "item_id" FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE,
  CONSTRAINT "booker_id" FOREIGN KEY (booker_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
  text VARCHAR(512) NOT NULL,
  item_id BIGINT NOT NULL,
  author_id BIGINT NOT NULL,
  created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  CONSTRAINT "item_id_comments" FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE,
  CONSTRAINT "author_id" FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE CASCADE
);
