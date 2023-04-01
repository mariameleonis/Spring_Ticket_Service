-- Insert sample users
INSERT INTO customer (name, email) VALUES
                                       ('John Doe', 'john.doe@example.com'),
                                       ('Alice Smith', 'alice.smith@example.com'),
                                       ('Bob Johnson', 'bob.johnson@example.com');

-- Insert sample events
INSERT INTO event (title, date, ticket_price) VALUES
                                                  ('Concert', '2022-05-15', 25.00),
                                                  ('Comedy show', '2022-06-10', 15.00),
                                                  ('Theater play', '2022-07-20', 20.00),
                                                  ('Nirvana Tribute', '2022-07-20', 50.00);

-- Insert sample tickets
INSERT INTO ticket (event_id, user_id, place) VALUES
                                                  (1, 1, 1),
                                                  (1, 2, 2),
                                                  (1, 3, 3),
                                                  (2, 1, 4),
                                                  (2, 2, 5),
                                                  (3, 3, 6);

-- Insert sample user accounts
INSERT INTO user_account (user_id, money) VALUES
                                              (1, 1000.00),
                                              (2, 500.00),
                                              (3, 200.00);