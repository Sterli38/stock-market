DROP TABLE if EXISTS participant;
DROP TABLE if EXISTS balance;

CREATE TABLE participant
(
    id            serial primary key,
    name          varchar not null,
    creation_date date    not null,
    password      varchar not null
);

INSERT INTO participant(name, creation_date, password)
values ('Egor', '2023-09-09', 'pasw123');

CREATE TABLE balance
(
    id             serial primary key,
    currency       varchar          not null,
    balance        double precision not null default 0,
    participant_id int references participant (id)
);

INSERT INTO balance(currency, balance, participant_id)
values ('EUR', 12.189, 1),
       ('USD', 16.12, 1),
       ('BTC', 1.12312, 1);






















