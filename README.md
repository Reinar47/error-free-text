# Error Free Text

Веб-приложение автоматической корректировки текста.
Принимает текст и язык (RU/EN), исправляет опечатки через Yandex Speller API,
отдаёт результат по идентификатору задачи.

## Стек

- Java 17
- Spring Boot 3.3
- Gradle
- PostgreSQL 16
- Liquibase (XML)
- MapStruct
- OpenAPI (Swagger UI)
- Docker / docker-compose

## Запуск через Docker

```bash
docker compose -f docker-compose.yaml up --build