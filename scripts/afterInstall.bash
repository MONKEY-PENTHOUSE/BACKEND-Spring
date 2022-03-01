docker pull monkeypenthouse/api-server-spring:v1
docker create -e spring.profiles.active=prod --publish 80:80 --name api-server-spring -i monkeypenthouse/api-server-spring:v1
docker cp api-server-spring:/key-pair.der /home/ubuntu/resources/
docker cp /home/ubuntu/resources/application-prod.properties api-server-spring:/
docker start api-server-spring