CREATE TABLE IF NOT EXISTS customer
                (
                id BIGSERIAL NOT NULL PRIMARY KEY
                name TEXT NOT NULL
                email TEXT NOT NULL
                );

CREATE TABLE IF NOT EXISTS car
                (id BIGSERIAL NOT NULL PRIMARY KEY
                model TEXT NOT NULL
                customer_id BIGINT CONSTRAINT customer_car_id REFERENCES customer);

INSERT INTO customer values ()

INSERT INTO customer (id,name,email) VALUES (1, 'Pasha', 'example@example.com',)