CREATE SCHEMA IF NOT EXISTS car_service;

create table if not exists car_service.car
(
    id          bigserial not null primary key,
    model       text      not null,
    customer_id bigint    not null

);
