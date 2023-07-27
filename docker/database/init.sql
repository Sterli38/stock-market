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

-- тестовые данные
INSERT INTO participant(name, creation_date, password)
values ('Egor', '2023-09-09', 'pasw123');

INSERT INTO history(operation_type_id, date, amount, participant_id, received_currency, given_currency, commission)
values (1, '2023-09-07', 50.0, 1, 'EUR', null, 0.0),
       (1, '2023-09-07', 1500.0, 1, 'RUB', null, 0.0),
       (1, '2023-09-07', 500.0, 1, 'USD', null, 0.0);























