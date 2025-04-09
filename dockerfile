# Sử dụng OpenJDK 21 làm base image
FROM openjdk:21-jdk-slim

# Đặt thư mục làm việc trong container
WORKDIR /app

# Copy file JAR từ thư mục build vào container
COPY ./target/authService-0.0.1-SNAPSHOT.jar /app/app.jar

# Mở cổng 3900
EXPOSE 3900

# Chạy ứng dụng Spring Boot
CMD ["java", "-jar", "/app/app.jar"]
