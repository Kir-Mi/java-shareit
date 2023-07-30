DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS comments CASCADE;

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(255)                            NOT NULL,
    email VARCHAR(512)                            NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT uq_user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    owner_id       BIGINT                                  NOT NULL,
    name        VARCHAR(255)                            NOT NULL,
    description varchar(1024)                           NOT NULL,
    is_available   BOOLEAN                                 NOT NULL,
    CONSTRAINT pk_item PRIMARY KEY (id),
    CONSTRAINT fk_owner_id_it FOREIGN KEY (owner_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS requests
(
    id               BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    requestor_id           BIGINT                                  NOT NULL,
    item_description varchar(1024)                           NOT NULL,
    created          TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    CONSTRAINT pk_request PRIMARY KEY (id),
    CONSTRAINT fk_user_id_rq FOREIGN KEY (requestor_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id             BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    booker_id         BIGINT                                  NOT NULL,
    item_id           BIGINT                                  NOT NULL,
    start_date          TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    end_date           TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    status VARCHAR(10)                             NOT NULL,
    CONSTRAINT pk_booking PRIMARY KEY (id),
    CONSTRAINT fk_booker_id_bk FOREIGN KEY (booker_id) REFERENCES users (id),
    CONSTRAINT fk_item_id_bk FOREIGN KEY (item_id) REFERENCES items (id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id      BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    item_id    BIGINT                                  NOT NULL,
    author_id  BIGINT                                  NOT NULL,
    text VARCHAR(2000)                                 NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    CONSTRAINT pk_comments PRIMARY KEY (id),
    CONSTRAINT fk_author_name_cm FOREIGN KEY (author_id) REFERENCES users (id),
    CONSTRAINT fk_item_id_cm FOREIGN KEY (item_id) REFERENCES items (id)
)