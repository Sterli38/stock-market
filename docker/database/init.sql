DROP TABLE if EXISTS transaction;
DROP TABLE if EXISTS participant;
DROP TABLE if EXISTS operation_type;
DROP TABLE If EXISTS role;

CREATE TABLE users
(
    username VARCHAR(50)  NOT NULL PRIMARY KEY,
    password VARCHAR(100) NOT NULL,
    enabled  BOOLEAN      NOT NULL
);

CREATE TABLE authorities
(
    username  VARCHAR(50) NOT NULL,
    authority VARCHAR(50) NOT NULL,
    CONSTRAINT fk_authorities_users FOREIGN KEY (username) REFERENCES users (username)
);

CREATE TABLE participant
(
    id            serial primary key,
    name          varchar                  not null unique,
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

INSERT INTO role(name)
values ('ADMIN'),
       ('USER');

INSERT INTO operation_type(type)
values ('DEPOSITING'),
       ('EXCHANGE'),
       ('WITHDRAWAL');

-- тестовые данные
INSERT INTO participant(name, role_id, creation_date, password)
values ('Egor', (SELECT id FROM role WHERE name = 'USER'), '2023-09-09', 'pasw123'),
       ('TestName', (SELECT id FROM role WHERE name = 'USER'), '2023-09-08', 'testPassword');

INSERT INTO transaction(operation_type_id, date, received_currency, received_amount, given_currency, given_amount,
                        participant_id, commission)
values (1, '2023-09-07', 'EUR', 50.0, null, null, 1, 2.5),-- пополнение
       (2, '2023-09-07', 'EUR', 20.58, 'RUB', 1500.0, 1, 75), -- обмен
       (2, '2023-09-07', 'RUB', 1315.636, 'EUR', 20, 1, 1), -- обмен
       (3, '2023-09-07', null, null, 'EUR', 5.0, 1, 0.25); -- вывод
