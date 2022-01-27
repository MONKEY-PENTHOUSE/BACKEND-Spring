docker pull monkeypenthouse/api-server-spring:v1
docker run -e spring.profiles.active=prod --publish 80:80 -i --detach --name api-server-spring monkeypenthouse/api-server-spring:v1
