DROP TABLE if EXISTS transaction;
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

CREATE TABLE transaction
(
    id                serial primary key,
    operation_type_id int REFERENCES operation_type (id) not null,
    date              timestamp                          not null,
    given_amount      double precision,
    received_amount   double precision,
    participant_id    int REFERENCES participant (id)    not null,
    received_currency varchar,
    given_currency    varchar,
    commission        double precision
);

INSERT INTO operation_type(type)
values ('DEPOSITING'),
       ('EXCHANGE'),
       ('WITHDRAWAL');

-- тестовые данные
INSERT INTO participant(name, creation_date, password)
values ('Egor', '2023-09-09', 'pasw123');

INSERT INTO transaction(operation_type_id, date, given_amount, received_amount, participant_id, given_currency, received_currency, commission)
values (1, '2023-09-07', 50.0, null, 1, 'EUR', null, 2.5),
       (2, '2023-09-07', 1500.0, 20.58, 1, 'RUB', 'EUR', 75),
       (2, '2023-09-07', 20, 1315.636, 1, 'EUR', 'RUB', 1),
       (3, '2023-09-07', 5.0, null, 1, 'EUR', null, 0.25);























