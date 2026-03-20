CREATE TABLE IF NOT EXISTS books (
                                     id      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                     title   VARCHAR(255)     NOT NULL,
    author  VARCHAR(255)     NOT NULL,
    isbn    VARCHAR(20)      NOT NULL UNIQUE,
    price   DOUBLE PRECISION NOT NULL,
    genre   VARCHAR(100),
    stock   INT DEFAULT 0
    );

CREATE TABLE IF NOT EXISTS orders (
                                      id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                      book_id       BIGINT           NOT NULL,
                                      customer_name VARCHAR(255)     NOT NULL,
    quantity      INT              NOT NULL,
    total_price   DOUBLE PRECISION NOT NULL,
    status        VARCHAR(50)      NOT NULL DEFAULT 'PENDING',
    created_at    TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_book FOREIGN KEY(book_id) REFERENCES books(id)
    );