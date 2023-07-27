DROP TABLE if EXISTS history;
DROP TABLE if EXISTS participant;
DROP TABLE if EXISTS operation_type;


CREATE TABLE participant
(
    id            serial primary key,
    name          varchar   not null,
    creation_date timestamp not null,
    password      varchar   not null
);

CREATE TABLE operation_type
(
    id   serial primary key,
    type varchar not null
);

CREATE TABLE history
(
    id                serial primary key,
    operation_type_id int REFERENCES operation_type (id) not null,
    date              timestamp                          not null,
    amount            double precision                   not null,
    participant_id    int REFERENCES participant (id)    not null,
    received_currency varchar,
    given_currency    varchar,
    commission        double precision
);

INSERT INTO operation_type(type)
values ('DEPOSITING'),
       ('EXCHANGE'),
       ('WITHDRAWAL');

INSERT INTO participant(name, creation_date, password)
values ('Egor', '2023-09-09', 'pasw123');