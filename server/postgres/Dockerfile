FROM postgres:10

RUN apt-get update && apt-get install curl libcurl4-openssl-dev -y

ENV ZOMBODB_DEB="https://www.zombodb.com/releases/v10-1.0.0b9/zombodb_jessie_pg10-10-1.0.0b9_amd64.deb"
ADD ${ZOMBODB_DEB} /tmp/zombodb.deb
RUN dpkg -i /tmp/zombodb.deb

ENV ELASTICSEARCH_URL="elasticsearch:9200"
ENV ELASTICSEARCH_TIMEOUT="30"

ENV POSTGRES_DB nextclass
COPY docker-entrypoint-initdb.d /docker-entrypoint-initdb.d
COPY wait-for-it.sh /usr/local/bin

ENTRYPOINT [ "/bin/bash" ]
CMD ["-c", "wait-for-it.sh -t $ELASTICSEARCH_TIMEOUT $ELASTICSEARCH_URL -- docker-entrypoint.sh postgres"]
