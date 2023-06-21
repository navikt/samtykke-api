FROM ghcr.io/navikt/baseimages/temurin:latest

ENV JAVA_OPTS='-XX:MaxRAMPercentage=90'

COPY build/libs/*.jar ./