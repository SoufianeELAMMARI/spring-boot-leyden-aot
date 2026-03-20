
--changeset bookstore:3
INSERT INTO books (title, author, isbn, price, genre, stock) VALUES
                                                                 ('Clean Code',                'Robert C. Martin', '978-0132350884', 35.99, 'Programming',  50),
                                                                 ('The Pragmatic Programmer',  'Andrew Hunt',      '978-0135957059', 42.00, 'Programming',  30),
                                                                 ('Domain-Driven Design',      'Eric Evans',       '978-0321125217', 55.00, 'Architecture', 20),
                                                                 ('Spring in Action',          'Craig Walls',      '978-1617294945', 49.99, 'Java',         40),
                                                                 ('Effective Java',            'Joshua Bloch',     '978-0134685991', 45.00, 'Java',         25),
                                                                 ('Designing Data-Intensive',  'Martin Kleppmann', '978-1449373320', 58.00, 'Architecture', 15);
