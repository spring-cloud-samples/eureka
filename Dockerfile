FROM    dsyer/java

MAINTAINER dsyer@pivotal.io

RUN apt-get install -y apt-utils
RUN apt-get -y install netcat

CMD java -jar /var/run/app/target/*.jar