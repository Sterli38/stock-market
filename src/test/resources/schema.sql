DROP TABLE if EXISTS participant;
DROP TABLE if exists history;

CREATE TABLE participant
(
    id            serial primary key,
    name          varchar not null,
    creation_date date    not null,
    password      varchar not null
);

CREATE TABLE history
(
    id                 serial primary key,
    date               timestamp        not null,
    amount             double precision not null,
    participant_id     int              not null,
    purchased_currency varchar          not null,
    payment_currency   varchar,
    commission         double precision
);