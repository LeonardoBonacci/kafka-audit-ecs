> docker build -t some-name .

> docker run -e ENV_CONNECTION=es:443 -e ENV_USERNAME=foo -e ENV_PASSWORD=bar -e ENV_BROKER=kafka:9092 -e ENV_KEY=shoe -e ENV_SECRET=car some-name