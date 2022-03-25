# Wait strategies

Testcontainers provides a set of wait strategies which help us determine if and when a container is ready to accept
requests. Wait strategies are defined for the `:wait-for` key in the container configuration with the
key `:wait-strategy` determining which strategy to select. The `start!` function will block until the container is ready
and continue processing afterwards.

## HTTP Wait Strategy

The HTTP wait strategy will perform an HTTP call according to the following configuration and only continue if the
criteria is met.

| Parameter         | Type       | Description                                             |
|-------------------|------------|---------------------------------------------------------|
| wait-strategy     | Keyword    | :http                                                   |
| path              | String     | The HTTP path to access                                 |
| port              | int        | The HTTP port to access                                 |
| method            | String     | The HTTP method to use (get, post, put...)              |
| status-codes      | seq of int | The status codes which mark a successful request        |
| tls               | boolean    | Should TLS be used?                                     |
| read-timeout      | long       | The duration in seconds the HTTP may take               |
| basic-credentials | Map        | {:username "User" :password "Password"}                 |
| headers           | Map        | HTTP Headers, e.g. {"Accept" "application/json"}        |
| startup-timeout   | long       | The duration in seconds the container may take to start |

Example:

```clojure
(tc/create {:image-name    "alpine:3.5"
            :command       ["/bin/sh"
                            "-c"
                            "while true ; do printf 'HTTP/1.1 200 OK\\n\\nyay' | nc -l -p 8080; done"]
            :exposed-ports [8080]
            :wait-for      {:wait-strategy   :http
                            :path            "/"
                            :port            8080
                            :method          "GET"
                            :status-codes    [200]
                            :tls             false
                            :read-timout     5
                            :headers         {"Accept" "text/plain"}
                            :startup-timeout 20}})
```

## Health Wait Strategy

The Health Wait Strategy uses a health check defined in the Dockerfile to determine if the container is ready.

| Parameter       | Type    | Description                                             |
|-----------------|---------|---------------------------------------------------------|
| wait-strategy   | Keyword | :port                                                   |
| startup-timeout | long    | The duration in seconds the container may take to start |

```clojure
(tc/create {:image-name    "alpine:3.5"
            :exposed-ports [8080]
            :wait-for      {:wait-strategy   :health
                            :startup-timeout 20}})
```

## Log Wait Strategy

The Log Wait Strategy waits until a certain phrase appears in the Docker containers' log.

| Parameter       | Type    | Description                                             |
|-----------------|---------|---------------------------------------------------------|
| wait-strategy   | Keyword | :log                                                    |
| message         | String  | A substring of an expected line in the containers log   |
| times           | int     | The number of times the predicate has to match          |
| startup-timeout | long    | The duration in seconds the container may take to start |

```clojure
(tc/create {:image-name    "postgres:12.2"
            :exposed-ports [5432]
            :env-vars      {"POSTGRES_PASSWORD" "pw"}
            :wait-for      {:wait-strategy   :log
                            :message         "accept connections"
                            :startup-timeout 10}})
```

## Port Wait Strategy

This strategy is the default selcted by Testcontainers if no wait strategy was defined. It waits for the first port in
the containers port mapping to be opened. It does not accept any parameters beside the `startup-timeout`

| Parameter       | Type    | Description                                             |
|-----------------|---------|---------------------------------------------------------|
| wait-strategy   | Keyword | :port                                                   |
| startup-timeout | long    | The duration in seconds the container may take to start |

```clojure
(tc/create {:image-name    "postgres:12.2"
            :exposed-ports [5432]
            :env-vars      {"POSTGRES_PASSWORD" "pw"}
            :wait-for      {:wait-strategy :port}})
```