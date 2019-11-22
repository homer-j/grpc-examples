#gRPC в качестве протокола межсервисного взаимодействия

##Сборка и запуск  сервера

1. зависимости
```
protobuf
grpc
```
2. сборка
```bash
$ cd echo-server
$ make
```
3. запуск 
```bash
$./echo_server
```

#Сборка и запуск  клиента

Для сборки и запуска клиента требуется установить java 11.

##Запуск клиента в режиме Unary call

```bash
./gradlew run
```

##Запуск клиента в режиме bidi streaming

```bash
./gradlew run --args=streaming
```

#Проверка функциональности
После запуска клиента и сервера в командной строке клиента
можно вводить и отправлять сообщения.
Команда `exit` позволяет выйти из программы-клиента.