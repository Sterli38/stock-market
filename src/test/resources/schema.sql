DROP TABLE if EXISTS participant;
DROP TABLE if EXISTS balance;
DROP TABLE if exists history;

CREATE TABLE participant
(
    id            identity primary key,
    name          varchar not null,
    creation_date date    not null,
    password      varchar not null
);

CREATE TABLE balance
(
    id             identity primary key,
    currency       varchar          not null,
    balance        double precision not null default 0,
    participant_id int references participant (id)
);

CREATE TABLE history
(
    id                 identity primary key,
    date               timestamp        not null,
    amount             double precision not null,
    participant_id     int              not null,
    purchased_currency varchar          not null,
    payment_currency   varchar,
    commission         double precision
);
