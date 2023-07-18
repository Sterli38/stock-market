DROP TABLE if EXISTS participant;
DROP TABLE if exists history;

CREATE TABLE participant
(
    id            serial primary key,
    name          varchar   not null,
    creation_date timestamp not null,
    password      varchar   not null
);

CREATE TABLE history
(
    id                serial primary key,
    operation_type    varchar                         not null,
    date              timestamp                       not null,
    amount            double precision                not null,
    participant_id    int REFERENCES participant (id) not null,
    received_currency varchar,
    given_currency    varchar,
    commission        double precision
);