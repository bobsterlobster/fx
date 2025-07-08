# JavaFX JDBC PostgreSQL Demo

Пример приложения на JavaFX, демонстрирующего работу с базой данных PostgreSQL через JDBC.  
Позволяет добавлять, загружать и удалять записи из трёх таблиц: фильмы, сериалы и игры.

---

## Описание проекта

Это JavaFX-приложение с табами для добавления новых данных и отдельной вкладкой для просмотра и удаления записей из базы данных PostgreSQL.

- Используются таблицы `movies`, `series` и `games` с полями:  
  `id`, `name`, `description`, `genre`.
- Добавление новых записей через форму.
- Загрузка и отображение данных из выбранной таблицы.
- Удаление выбранной записи из базы и таблицы.
- Кастомная стилизация интерфейса с помощью CSS.

---

## Требования

- Java 11 и выше
- PostgreSQL 9.6 и выше
- JDBC драйвер для PostgreSQL
- Maven или Gradle для сборки (опционально)
- JavaFX SDK

---

## Настройка базы данных

1. Создайте базу данных `postgres` (если ещё нет).
2. Создайте таблицы в вашей базе данных с помощью следующего SQL (пример для таблицы `movies`):

```sql
CREATE TABLE movies (
  id VARCHAR PRIMARY KEY,
  name VARCHAR NOT NULL,
  description TEXT,
  genre VARCHAR
);

CREATE TABLE series (
  id VARCHAR PRIMARY KEY,
  name VARCHAR NOT NULL,
  description TEXT,
  genre VARCHAR
);

CREATE TABLE games (
  id VARCHAR PRIMARY KEY,
  name VARCHAR NOT NULL,
  description TEXT,
  genre VARCHAR
);
