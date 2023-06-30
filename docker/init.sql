DROP TABLE if EXISTS participant;
DROP TABLE if EXISTS balance;

CREATE TABLE participant
(
    id            serial primary key,
    name          varchar not null,
    creation_date timestamp    not null,
    password      varchar not null
);

INSERT INTO participant(name, creation_date, password)
values ('Egor', '2023-09-09', 'pasw123');

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

INSERT INTO history(date, amount, participant_id, purchased_currency, payment_currency, commission)
values ('2023-10-22 10:00:01', '100.23', 1, 'EUR', 'USD', '0.434'),
       ('2023-09-15 22:34:03', '12.25', 1, 'EUR', 'USD', '0.42'),
       ('2023-03-10 20:54:33', '22.25', 1, 'EUR', 'USD', '0.8');























