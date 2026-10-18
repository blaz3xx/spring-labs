DROP TABLE IF EXISTS posts CASCADE;
DROP TABLE IF EXISTS topics CASCADE;

CREATE TABLE topics (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(100),
    category VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE posts (
    id SERIAL PRIMARY KEY,
    topic_id INT NOT NULL,
    content TEXT NOT NULL,
    author VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_topic FOREIGN KEY (topic_id) REFERENCES topics(id) ON DELETE CASCADE
);

INSERT INTO topics (title, author, category) VALUES 
('Як налаштувати PostgreSQL', 'Адмін', 'STUDY'),
('Обговорення нових ігор', 'Геймер', 'GAMES');

INSERT INTO posts (topic_id, content, author) VALUES 
(1, 'Чудовий фреймворк!', 'Іван'),
(1, 'Підкажіть, як налаштувати JdbcClient?', 'Мирослав'),
(2, 'Використовуйте pgAdmin для керування.', 'Влад');