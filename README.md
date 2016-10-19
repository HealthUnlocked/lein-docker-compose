# lein-docker-compose

[![Clojars Project](https://img.shields.io/clojars/v/healthunlocked/lein-docker-compose.svg)](https://clojars.org/healthunlocked/lein-docker-compose)
[![CircleCI](https://circleci.com/gh/HealthUnlocked/lein-docker-compose.svg?style=svg)](https://circleci.com/gh/HealthUnlocked/lein-docker-compose)

Small leiningen plugin to help make using `docker-compose` with your Clojure projects easier :cat2:.

It's possible to tell `docker-compose` explicitly which ports on your host machine to map container ports to, but this means you can't have more than one app running that uses the same ports.
Letting `docker-compose` auto-allocate the ports means you'll never have this kind of clash, but the ports will change.

`lein-docker-compose` makes your Clojure app aware of the generated ports by discovering the port mappings created by `docker-compose` and injecting them into `environ.core/env`.

## Requirements

You'll need `docker` and `docker-compose`, and the latest versions of `environ`, `lein-environ`, and `lein-docker-compose` in your `project.clj`, like this:

```clojure
:dependencies [[environ "1.1.0"]]
:plugins      [[lein-environ "1.1.0"]
               [lein-docker-compose "0.1.0"]]
```

## Usage

Define the services your app needs, and the ports they expose, in your `docker-compose.yml`. For example, to start a RabbitMQ cluster, you could do something like this:

```yml
# docker-compose.yml
rabbit:
  image: rabbitmq
  ports:
    - "5672"
    - "15672"
  environment:
    - RABBITMQ_DEFAULT_USER=user
    - RABBITMQ_DEFAULT_PASS=password
```

Start these services with `docker-compose up -d` (or miss off the `-d` if you want to start them in the foreground).

Then just start your Clojure app normally, via `lein repl` or `lein run`. Check the contents of `environ.core/env` and you'll see that `lein-docker-compose` has injected port mappings. For the above example you would see something like this:

```clojure
(require '[environ.core :refer [env]])
(:docker-rabbit-port-5672 env)
;; => "174738" (or some other random port)
(:docker-rabbit-port-15672 env)
;; => "174739" (or some other random port)
```

## Contributions

Contributions welcome... emojis in commit messages encouraged. :dog:
