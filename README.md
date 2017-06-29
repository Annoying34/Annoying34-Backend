# Annoying 34 Backend
____

## Server - Setup

#### MySQL

```
$ mkdir -p /storage/docker/mysql-datadir
$ docker run -d --name=annoying34-mysql --env="MYSQL_ROOT_PASSWORD=FuerNervigeMailsBrauchtManEineDatenbank" -p 3306:3306 --volume=/storage/docker/mysql-datadir:/var/lib/mysql mysql
```

___

#### Server

```
$ docker run -d -p 8080:8080 --link annoying34-mysql:mysql annoying34/annoying34-backend
```

#### Frontend

```
$ docker run -d -p 80:3000 annoying34/annoying34-frontend
```
