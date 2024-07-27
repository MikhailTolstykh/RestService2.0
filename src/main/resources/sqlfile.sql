
create table if not exists car
(
    id          bigserial not null primary key,
    model       text      not null,
    customer_id bigint    not null

);
