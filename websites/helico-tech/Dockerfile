# Build JVM
FROM gradle:8-alpine AS build

# Create a custom Java runtime
RUN $JAVA_HOME/bin/jlink \
         --add-modules java.base \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output /javaruntime

WORKDIR /app

COPY . .

RUN gradle :websites:helico-tech:buildFatJar

FROM alpine:3.21.0

# Install Java runtime
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH "${JAVA_HOME}/bin:${PATH}"
COPY --from=build /javaruntime $JAVA_HOME

# Copy the built jar file
WORKDIR /app

COPY --from=build /app/websites/helico-tech/build/libs/helico-tech-all.jar .

CMD ["java", "-jar", "helico-tech-all.jar"]