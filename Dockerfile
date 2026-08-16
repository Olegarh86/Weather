FROM tomcat:11.0.14-jre17

WORKDIR /app

RUN rm -rf /usr/local/tomcat/webapps/*

COPY target/weather.war /usr/local/tomcat/webapps/weather.war
