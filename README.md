# Тестовый проект на spring boot

## База данных
Для запуска контейнера с БД необходимо выполнить след команду на создание контейнера

```commandline
docker run --name cashcards-postgres -v pg-data:/var/lib/postgresql/data -e POSTGRES_PASSWORD=mysecretpassword -p 5432:5432  -d  postgres
```

и затем подключившись следующей командой 
```commandline
docker exec -it cashcards-postgres  psql -U postgres
```
создать в ней таблицы
```postgresql
create database cashcards;
create user cashcards with password 'Welcome1';
grant all privileges on database cashcards to cashcards;
```
Переключиться в саму БД cashcards и дать привилегий дефолтной схеме
```postgresql
GRANT ALL PRIVILEGES ON SCHEMA public TO cashcards;
```