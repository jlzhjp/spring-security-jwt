FROM ubuntu:latest
LABEL authors="jvj01"

ENTRYPOINT ["top", "-b"]