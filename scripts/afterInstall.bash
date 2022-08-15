docker pull monkeypenthouse/api-server-spring:v1
docker create -e spring.profiles.active=prod --publish 80:80 -v /home/ec2-user/log:/home --name api-server-spring -i monkeypenthouse/api-server-spring:v1
docker cp /home/ec2-user/resources/key-pair.der api-server-spring:/
docker cp /home/ec2-user/resources/application-prod.properties api-server-spring:/
docker start api-server-spring
