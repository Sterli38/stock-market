DROP TABLE If Exists participant_to_role;
DROP TABLE if EXISTS transaction;
DROP TABLE if EXISTS participant;
DROP TABLE if EXISTS operation_type;
DROP TABLE If EXISTS role;

CREATE TABLE role
(
    id        serial primary key,
    role_name varchar not null
);

CREATE TABLE participant
(
    id            serial primary key,
    name          varchar   not null unique,
    password      varchar   not null,
    creation_date timestamp not null,
    enabled       boolean   not null
);

CREATE TABLE participant_to_role
(
    id             serial primary key,
    participant_id int REFERENCES participant (id) not null,
    role_id        int REFERENCES role (id)        not null
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

INSERT INTO role(role_name)
values ('ADMIN'),
       ('USER'),
       ('READER');

INSERT INTO operation_type(type)
values ('DEPOSITING'),
       ('EXCHANGE'),
       ('WITHDRAWAL');

-- тестовые данные
INSERT INTO participant(name, password, creation_date, enabled)
values ('egor', '$2y$10$XMZ3YMbr8w0klLv7xwJ/Pepun0V1Ip3bCcAerEjQ7irZMkLaOuvtO', '2023-09-09', 'true'), -- egor
       ('testName', '$2y$10$xmysiQBmQTF4BSr2UV9df.mLG2DVrXpDkqxB2sX1BEh2dDKUa5xVi', '2023-09-08', 'false'); -- testName

INSERT INTO participant_to_role(participant_id, role_id)
values (1, 1),
       (1, 2),
       (2, 3);

INSERT INTO transaction(operation_type_id, date, received_currency, received_amount, given_currency, given_amount,
                        participant_id, commission)
values (1, '2023-09-07', 'EUR', 50.0, null, null, 1, 2.5),-- пополнение
       (1, '2023-09-07', 'RUB', 150000, null, null, 1, 0), -- пополнение
       (2, '2023-09-07', 'EUR', 20.58, 'RUB', 1500.0, 1, 75), -- обмен
       (2, '2023-09-07', 'RUB', 1315.636, 'EUR', 20, 1, 1), -- обмен
       (3, '2023-09-07', null, null, 'EUR', 5.0, 1, 0.25); -- вывод
