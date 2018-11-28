#编译系统
FROM gradle:jdk8 as builder

ADD . /build/
WORKDIR /build/
USER root
RUN ["./gradlew","--no-daemon","build"]
#到了这里的时候肯定是线上版本了，所以直接打包

FROM java:8-jre

COPY --from=builder /build/tools/build/libs/tools-1.0-SNAPSHOT.jar /deploy/tools.jar
COPY ./tools/src/main/shell/deploy.sh /usr/bin/deploy.sh
RUN ["chmod","+x","/usr/bin/deploy.sh"]

CMD ["sh"]
#ENTRYPOINT ["java","-jar","/deploy/exporter.jar"]
