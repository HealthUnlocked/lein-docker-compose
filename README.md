# lein-docker-compose

[![Clojars Project](https://img.shields.io/clojars/v/healthunlocked/lein-docker-compose.svg)](https://clojars.org/healthunlocked/lein-docker-compose)

Small leiningen plugin to help make using `docker-compose` with your Clojure projects easier :cat2:.

It's possible to tell `docker-compose` explicitly which ports on your host machine to map container ports to, but this means you can't have more than one app running that uses the same ports.
Letting `docker-compose` auto-allocate the ports means you'll never have this kind of clash, but the ports will change.

`lein-docker-compose` makes your Clojure app aware of the generated ports by discovering the port mappings created by `docker-compose` and injecting them into `environ.core/env`.

It does this in two steps:

1. When you call `lein docker-compose`, it parses your `docker-compose.yml` and writes the ports it discovers to `.lein-docker-env`.
2. When you start a repl or otherwise run your code in dev mode, it reads this file and merges its contents into environ's `env`.

# Breaking change in 0.2.0!

From 0.2.0, the first step is run only on-demand. This is because it takes a noticeable fraction of a second to run, and was ending up being called multiple times on REPL startup due to multiple calls to the environ function this plugin wraps.

In future, to guarantee it's re-run every time it needs to, you need to start your repl/app like this: `lein do docker-compose, repl`.

Or just run it when you know the ports have changed!

## Requirements

You'll need `docker` and `docker-compose`. You'll also need either `environ` or `yogthos/config`, and`lein-environ` and `lein-docker-compose` in your `project.clj`.

```clojure
:profiles {:dev {::plugins [[lein-environ "1.1.0"]
                            [lein-docker-compose "0.2.0"]]}}
```

## Usage

Define the services your app needs, and the ports they expose, in your `docker-compose.yml`. For example, to start a RabbitMQ cluster, you could do something like this:

```yml
# docker-compose.yml
services:
  rabbit:
    image: rabbitmq
    ports:
      - "5672"
      - "15672"
    environment:
      - RABBITMQ_DEFAULT_USER=user
      - RABBITMQ_DEFAULT_PASS=password
```

(N.B. the old format of docker-compose.yml put services at the top level of the file - lein-docker-compose supports both formats).

Start these services with `docker-compose up -d` (or miss off the `-d` if you want to start them in the foreground).

Then just start your Clojure app normally, via `lein repl` or `lein run`. Check the contents of `environ.core/env` and you'll see that `lein-docker-compose` has injected port mappings. For the above example you would see something like this:

```clojure
(require '[config.core :refer [env]])
(:docker-rabbit-port-5672 env)
;; => "174738" (or some other random port)
(:docker-rabbit-port-15672 env)
;; => "174739" (or some other random port)
```

## Contributions

Contributions welcome... emojis in commit messages encouraged. :dog:
