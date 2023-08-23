DROP TABLE if EXISTS transaction;
DROP TABLE if EXISTS participant;
DROP TABLE if EXISTS operation_type;

CREATE TABLE role
(
    id   serial primary key,
    name varchar not null
);
INSERT INTO role(name)
values ('ADMIN'),
       ('USER');


CREATE TABLE participant
(
    id            serial primary key,
    name          varchar                  not null,
    role_id       int REFERENCES role (id) not null,
    creation_date timestamp                not null,
    password      varchar                  not null
);

CREATE TABLE operation_type
(
    id   serial primary key,
    type varchar not null
);

CREATE TABLE transaction
(
    id                serial primary key,
    operation_type_id int REFERENCES operation_type (id) not null,
    date              timestamp                          not null,
    received_currency varchar,
    received_amount   double precision,
    given_currency    varchar,
    given_amount      double precision,
    participant_id    int REFERENCES participant (id)    not null,
    commission        double precision
);

INSERT INTO operation_type(type)
values ('DEPOSITING'),
       ('EXCHANGE'),
       ('WITHDRAWAL');

INSERT INTO participant(name, role_id, creation_date, password)
values ('Egor', (SELECT id FROM role WHERE name = 'USER'), '2023-09-09', 'pasw123'),
       ('TestName', (SELECT id FROM role WHERE name = 'USER'), '2023-09-08', 'testPassword');