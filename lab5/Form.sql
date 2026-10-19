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
    CONSTRAINT fk_topic FOREIGN KEY (topic_id) REFERENCES topics(id)
);

INSERT INTO topics (title, author, category) VALUES
('Як налаштувати PostgreSQL для Spring Boot', 'Адмін', 'STUDY'),
('JdbcTemplate чи JdbcClient: що обрати?', 'Аня', 'STUDY'),
('Транзакції у Spring: питання до лаби', 'Іван', 'STUDY'),
('Найкращі інді-ігри цього року', 'Геймер', 'GAMES'),
('Шукаю команду для кооперативу', 'Влад', 'GAMES'),
('Обговорення нових ігор', 'Геймер', 'GAMES'),
('Spring Security: з чого почати?', 'Аня', 'STUDY'),
('Де поїсти біля корпусу', 'Мирослав','OTHER'),
('Оголошення: зміна розкладу', 'Адмін', 'OTHER'),
('Порожня тема без відповідей', 'Іван', 'OTHER');

INSERT INTO posts (topic_id, content, author) VALUES
(1, 'Використовуйте pgAdmin або DBeaver для керування базою.', 'Влад'),
(1, 'Не забудьте вказати порт у spring.datasource.url.', 'Аня'),
(1, 'Дякую, запрацювало!', 'Мирослав'),
(2, 'JdbcClient зручніший завдяки fluent API.', 'Іван'),
(2, 'А JdbcTemplate дає більше контролю, наприклад batchUpdate.', 'Адмін'),
(3, 'Чому @Transactional не спрацьовує при виклику з того ж класу?', 'Аня'),
(3, 'Бо виклик через this обходить проксі.', 'Адмін'),
(4, 'Hollow Knight: Silksong нарешті вийшов!', 'Геймер'),
(4, 'Раджу Balatro.', 'Влад'),
(5, 'Я в ділі, пиши в особисті.', 'Іван'),
(6, 'Чекаю на нову частину серії.', 'Мирослав'),
(7, 'Почніть з офіційного гайду на spring.io.', 'Іван'),
(8, 'Їдальня на першому поверсі норм.', 'Влад'),
(8, 'Краще кав''ярня через дорогу.', 'Аня'),
(9, 'Пари у середу перенесено на четвер.', 'Адмін');