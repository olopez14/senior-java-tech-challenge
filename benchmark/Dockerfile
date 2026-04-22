FROM eclipse-temurin:21-jre
RUN apt-get update && apt-get install -y curl bash bc && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY benchmark.sh .

ENTRYPOINT ["bash", "benchmark.sh"]


