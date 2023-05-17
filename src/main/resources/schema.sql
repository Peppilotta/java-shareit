DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS requests;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS users;



CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    name  VARCHAR(255)                                        NOT NULL,
    email VARCHAR(512) UNIQUE                                 NOT NULL
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    description  VARCHAR(255) NOT NULL,
    requestor_id BIGINT       NOT NULL
);

CREATE TABLE IF NOT EXISTS items
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    description  VARCHAR(255) NOT NULL,
    is_available boolean      NOT NULL,
    owner_id     BIGINT,
    request_id   BIGINT
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id    BIGINT,
    booker_id  BIGINT,
    status     VARCHAR(12)
);

CREATE TABLE IF NOT EXISTS comments
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    text         VARCHAR(512)                NOT NULL,
    item_id      BIGINT,
    author_id    BIGINT,
    created_date TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

ALTER TABLE items
    DROP CONSTRAINT IF EXISTS fk_items_to_users;

ALTER TABLE items
    ADD CONSTRAINT fk_items_to_users FOREIGN KEY (owner_id) REFERENCES users (id)
        ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE bookings
    DROP CONSTRAINT IF EXISTS fk_bookings_to_items;

ALTER TABLE bookings
    ADD CONSTRAINT fk_bookings_to_items FOREIGN KEY (item_id) REFERENCES items (id)
        ON DELETE CASCADE ON UPDATE CASCADE;


ALTER TABLE bookings
    DROP CONSTRAINT IF EXISTS fk_bookings_to_users;

ALTER TABLE bookings
    ADD CONSTRAINT fk_bookings_to_users FOREIGN KEY (booker_id) REFERENCES users (id)
        ON DELETE CASCADE ON UPDATE CASCADE;


ALTER TABLE comments
    DROP CONSTRAINT IF EXISTS fk_comments_to_items;

ALTER TABLE comments
    ADD CONSTRAINT fk_comments_to_items FOREIGN KEY (item_id) REFERENCES items (id)
        ON DELETE CASCADE ON UPDATE CASCADE;


ALTER TABLE comments
    DROP CONSTRAINT IF EXISTS fk_comments_to_users;

ALTER TABLE comments
    ADD CONSTRAINT fk_comments_to_users FOREIGN KEY (author_id) REFERENCES users (id)
        ON DELETE CASCADE ON UPDATE CASCADE;


ALTER TABLE requests
    DROP CONSTRAINT IF EXISTS fk_request_to_users;

ALTER TABLE requests
    ADD CONSTRAINT fk_request_to_users FOREIGN KEY (requestor_id) REFERENCES users (id)
        ON DELETE CASCADE ON UPDATE CASCADE;