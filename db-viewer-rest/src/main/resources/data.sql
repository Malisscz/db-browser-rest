DROP TABLE IF EXISTS DB_CONNECTION;

CREATE TABLE DB_CONNECTION (
                              id INT AUTO_INCREMENT  PRIMARY KEY,
                              name VARCHAR(255) NOT NULL,
                              database_Name VARCHAR(255) NOT NULL,
                              username VARCHAR(255) NOT NULL,
                              password VARCHAR(255) NOT NULL,
                              hostname VARCHAR(255) NOT NULL,
                              port VARCHAR(255) NOT NULL
);
/*
INSERT INTO DB_CONNECTION (name, full_url, type) VALUES
('Standardni H2', 'Dangote', 'Billionaire Industrialist'),
('Nevim', 'Gates', 'MySQL'),
('Folrunsho', 'Alakija', 'h2');*/