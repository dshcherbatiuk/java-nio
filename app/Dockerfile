FROM alpine

RUN apk update
RUN apk add --upgrade tcpdump
RUN apk --no-cache add openjdk11 --repository=http://dl-cdn.alpinelinux.org/alpine/edge/community

ENV APP_DIR /app

WORKDIR ${APP_DIR}

ENV BUILD_NAME_SHORT app
ENV BUILD_NAME ${BUILD_NAME_SHORT}-1.0.0-SNAPSHOT.jar

WORKDIR ${APP_DIR}
ADD ./build/libs/${BUILD_NAME} ${APP_DIR}/${BUILD_NAME}

CMD java $JVM_ARGS -jar ${APP_DIR}/${BUILD_NAME}