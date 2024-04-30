# clj-test-containers

[![Clojars Project](http://clojars.org/clj-test-containers/latest-version.svg)](http://clojars.org/clj-test-containers)

## What it is

This library is a lightweight wrapper around the [Testcontainers Java library](https://www.testcontainers.org/).

## What it isn't

This library does not provide tools to include testcontainers in your testing lifecycle. As there are many different
test tools with different approaches to testing in the clojure world, handling the lifecycle is up to you.

## Integration with test runners

There is an [experimental kaocha plugin](https://github.com/lambdaschmiede/kaocha-testcontainers-plugin) you can try out

## Usage

The library provides a set of functions to interact with the testcontainers. A simple example, how to create a container
with a Docker label, could look like this:

```clojure
(require '[clj-test-containers.core :as tc])

(def container (-> (tc/create {:image-name    "postgres:12.1"
                               :exposed-ports [5432]
                               :env-vars      {"POSTGRES_PASSWORD" "verysecret"}})
                   (tc/bind-filesystem! {:host-path      "/tmp"
                                         :container-path "/opt"
                                         :mode           :read-only})
                   (tc/start!)))

(do-database-testing (:host container)
                     (get (:mapped-ports container) 5432))

(tc/stop! container)
```

If you'd rather create a container from a Dockerfile in your project, it could look like this:

```clojure

(require '[clj-test-containers.core :as tc])

(def container (-> (tc/create-from-docker-file {:env-vars      {"FOO" "bar"}
                                                :exposed-ports [80]
                                                :docker-file   "resources/Dockerfile"})
                   (tc/start!)))
```

If you prefer to use prebuilt containers from the Testcontainers project, you can do it like this

```clojure
(require '[clj-test-containers.core :as tc])
(:import [org.testcontainers.containers PostgreSQLContainer])

(def container (-> (tc/init {:container     (PostgreSQLContainer. "postgres:12.2")
                             :exposed-ports [5432]})
                   (tc/start!)))
```

## Functions and Properties

### create

Creates a testcontainers instance from a given Docker label and returns them

#### Config parameters:

| Key                | Type                        | Description                                                                                         |
| -------------      | :-------------              |:----------------------------------------------------------------------------------------------------|
| `:image-name`      | String, mandatory           | The name and label of an image, e.g. `postgres:12.2`                                                |
| `:exposed-ports`   | Vector with ints, mandatory | All ports which should be exposed and mapped to a local port                                        |
| `:env-vars`        | Map                         | A map with environment variables                                                                    |
| `:command`         | Vector with strings         | The start command of the container                                                                  |
| `:network`         | Map                         | A map containing the configuration of a Docker Network (see: `create-network`)                      |
| `:network-aliases` | Map                         | A list of alias names for the container on the network                                              |
| `:wait-for`        | Map                         | A map containing the [wait strategy](doc/wait-strategies.md) to use and the condition to check for |
| `:log-to`          | Map                         | A map containing the [log strategy](doc/log-strategies.md) to use, e.g. {:log-strategy string}     |

#### Result:

| Key              | Type                                      | Description                                                                               |
| -------------    | :-------------                            | :-----                                                                                    |
| `:container`     | `org.testcontainers.containers.Container` | The Testcontainers instance, accessible for everything this library doesn't provide (yet) |
| `:exposed-ports` | Vector with ints                          | Value of the same input parameter                                                         |
| `:env-vars`      | Map                                       | Value of the same input parameter                                                         |
| `:host`          | String                                    | The host for the Docker Container                                                         |
| `:network`       | Map                                       | The network configuration of the Container, if provided                                   |
| `:wait-for`      | Map                                       | The wait-for configuration of the Container, if provided!                                 |

#### Example:

```clojure
(create {:image-name      "alpine:3.2"
         :exposed-ports   [80]
         :env-vars        {"MAGIC_NUMBER" "42"}
         :network         (create-network)
         :network-aliases ["api-server"]
         :command         ["/bin/sh"
                           "-c"
                           "while true; do echo \"$MAGIC_NUMBER\" | nc -l -p 80; done"]})
```

#### Example using wait-for and healthcheck:

```clojure
(create {:image-name      "alpine:3.2"
         :exposed-ports   [80]
         :env-vars        {"MAGIC_NUMBER" "42"}
         :network         (create-network)
         :network-aliases ["api-server"]
         :wait-for        {:strategy :health}
         :command         ["/bin/sh"
                           "-c"
                           "while true; do echo \"$MAGIC_NUMBER\" | nc -l -p 80; done"]})
```

### init

Initializes a given Testcontainer, which was e.g. provided by a library

#### Config parameters:

| Key                | Type                                                        | Description                                                                                         |
| -------------      | :-------------                                              |:----------------------------------------------------------------------------------------------------|
| `:container`       | `org.testcontainers.containers.GenericContainer`, mandatory | The name and label of an image, e.g. `postgres:12.2`                                                |
| `:exposed-ports`   | Vector with ints, mandatory                                 | All ports which should be exposed and mapped to a local port                                        |
| `:env-vars`        | Map                                                         | A map with environment variables                                                                    |
| `:command`         | Vector with strings                                         | The start command of the container                                                                  |
| `:network`         | Map                                                         | A map containing the configuration of a Docker Network (see: `create-network`)                      |
| `:network-aliases` | Map                                                         | A list of alias names for the container on the network                                              |
| `:wait-for`        | Map                                                         | A map containing the [wait strategy](doc/wait-strategies.md) to use and the condition to check for |
| `:log-to`          | Map                                                         | A map containing the [log strategy](doc/log-strategies.md) to use, e.g. {:log-strategy string}                               |
|                    |                                                             |                                                                                                     |

#### Result:

| Key              | Type                                      | Description                                                                               |
| -------------    | :-------------                            |:------------------------------------------------------------------------------------------|
| `:container`     | `org.testcontainers.containers.Container` | The Testcontainers instance, accessible for everything this library doesn't provide (yet) |
| `:exposed-ports` | Vector with ints                          | Value of the same input parameter                                                         |
| `:env-vars`      | Map                                       | Value of the same input parameter                                                         |
| `:host`          | String                                    | The host for the Docker Container                                                         |
| `:network`       | Map                                       | The network configuration of the Container, if provided                                   |
| `:wait-for`      | Map                                       | The wait-for configuration of the Container, if provided!                                 |

#### Example:

```clojure
;; PostgreSQL container needs a separate library! This is not included.
(init {:container     (org.testcontainers.containers.PostgreSQLContainer)
       :exposed-ports [80]
       :env-vars      {"MAGIC_NUMBER" "42"}
       :command       ["/bin/sh"
                       "-c"
                       "while true; do echo \"$MAGIC_NUMBER\" | nc -l -p 80; done"]})
```

#### Example using wait-for and a log message:

```clojure
;; PostgreSQL container needs a separate library! This is not included.
(init {:container     (org.testcontainers.containers.PostgreSQLContainer)
       :exposed-ports [80]
       :env-vars      {"MAGIC_NUMBER" "42"}
       :wait-for      {:strategy :log :message "accept connections"}
       :command       ["/bin/sh"
                       "-c"
                       "while true; do echo \"$MAGIC_NUMBER\" | nc -l -p 80; done"]})
```

### create-from-docker-file

Creates a testcontainer from a Dockerfile

#### Config parameters:

| Key                | Type                        | Description                                                                    |
| -------------      | :-------------              | :-----                                                                         |
| `:docker-file`     | String, mandatory           | String containing a path to a Dockerfile                                       |
| `:exposed-ports`   | Vector with ints, mandatory | All ports which should be exposed and mapped to a local port                   |
| `:env-vars`        | Map                         | A map with environment variables                                               |
| `:command`         | Vector with strings         | The start command of the container                                             |
| `:network`         | Map                         | A map containing the configuration of a Docker Network (see: `create-network`) |
| `:network-aliases` | Map                         | A list of alias names for the container on the network                         |
| `:wait-for`        | Map                         | A map containing the [wait strategy](doc/wait-strategies.md) to use and the condition to check for       |
| `:log-to`          | Map                         | A map containing the [log strategy](doc/log-strategies.md) to use, e.g. {:log-strategy string}          |
|                    |                             |                                                                                |

#### Result:

| Key              | Type                                      | Description                                                                               |
| -------------    | :-------------                            | :-----                                                                                    |
| `:container`     | `org.testcontainers.containers.Container` | The Testcontainers instance, accessible for everything this library doesn't provide (yet) |
| `:exposed-ports` | Vector with ints                          | Value of the same input parameter                                                         |
| `:env-vars`      | Map                                       | Value of the same input parameter                                                         |
| `:host`          | String                                    | The host for the Docker Container                                                         |
| `:network`       | Map                                       | The network configuration of the Container, if provided                                   |
| `:wait-for`      | Map                                       | The wait-for configuration of the Container, if provided!                                 |

#### Example:

```clojure
(create-from-docker-file {:docker-file   "resources/Dockerfile"
                          :exposed-ports [5432]
                          :env-vars      {"MAGIC_NUMBER" "42"}
                          :command       ["/bin/sh"
                                          "-c"
                                          "while true; do echo \"$MAGIC_NUMBER\" | nc -l -p 80; done"]})
```

---

### start!

Starts the Testcontainer, which was defined by `create`

#### Config parameters:

| Key                | Type           | Description                           |
| -------------      | :------------- | :-----                                |
| First parameter:   |                |                                       |
| `container-config` | Map, mandatory | Return value of the `create` function |
|                    |                |                                       |

#### Result:

| Key              | Type                                      | Description                                                                               |
| -------------    | :-------------                            | :-----                                                                                    |
| `:container`     | `org.testcontainers.containers.Container` | The Testcontainers instance, accessible for everything this library doesn't provide (yet) |
| `:exposed-ports` | Vector with ints                          | Value of the same input parameter                                                         |
| `:env-vars`      | Map                                       | Value of the same input parameter                                                         |
| `:host`          | String                                    | The host for the Docker Container                                                         |
| `:id`            | String                                    | The ID of the started docker container                                                    |
| `:mapped-ports`  | Map                                       | A map containing the container port as key and the mapped local port as a value           |

#### Example:

```clojure
(def container (create {:image-name    "alpine:3.2"
                        :exposed-ports [80]
                        :env-vars      {"MAGIC_NUMBER" "42"}}))

(start! container)
```

---

### stop!

Stops the Testcontainer, which was defined by `create`

#### Config parameters:

| Key                | Type           | Description                           |
| -------------      | :------------- | :-----                                |
| First parameter:   |                |                                       |
| `container-config` | Map, mandatory | Return value of the `create` function |

#### Result:

The `container-config`

#### Example:

```clojure
(def container (create {:image-name    "alpine:3.2"
                        :exposed-ports [80]
                        :env-vars      {"MAGIC_NUMBER" "42"}}))

(start! container)
(stop! container)
```

---

### map-classpath-resource!

Maps a resource from your classpath into the containers file system

#### Config parameters:

| Key        | Type                | Description  |
| ------------- 		|:-------------		| :-----|
| First parameter: | | |
| `container-config`| Map, mandatory | Return value of the `create` function |
| Second parameter: | | |
| `:resource-path`        | String, mandatory                | Path of your classpath resource |
| `:container-path`    | String, mandatory | Path, to which the resource should be mapped |
| `:mode`        | Keyword, mandatory                | `:read-only` or `:read-write` |

#### Result:

The `container-config`

#### Example:

```clojure
(map-classpath-resource! container {:resource-path  "test.sql"
                                    :container-path "/opt/test.sql"
                                    :mode           :read-only})
```

---

### bind-filesystem!

Binds a path from your local filesystem into the Docker container as a volume

#### Config parameters:

| Key                | Type               | Description                                  |
| -------------      | :-------------     | :-----                                       |
| First parameter:   |                    |                                              |
| `container-config` | Map, mandatory     | Return value of the `create` function        |
| Second parameter:  |                    |                                              |
| `:host-path`       | String , mandatory | Path on your local filesystem                |
| `:container-path`  | String, mandatory  | Path, to which the resource should be mapped |
| `:mode`            | Keyword, mandatory | `:read-only` or `:read-write`                |

#### Result:

The `container-config`

#### Example:

```clojure
(bind-filesystem! container {:host-path      "."
                             :container-path "/opt"
                             :mode           :read-only})
```

---

### copy-file-to-container!

Copies a file from your filesystem or classpath into the running container

#### Config parameters:

| Key        | Type                | Description  |
| ------------- 		|:-------------		| :-----|
| First parameter: | | |
| `container-config`| Map, mandatory | Return value of the `create` function |
| Second parameter: | | |
| `:path`            | String, mandatory | Path to a classpath resource *or* file on your filesystem |
| `:container-path`        | String, mandatory                | Path, to which the file should be copied |
| `:type`            | Keyword, mandatory                | `:classpath-resource` or `:host-path` |

#### Result:

The `container-config`

#### Example:

```clojure
(copy-file-to-container! container {:path           "test.sql"
                                    :container-path "/opt/test.sql"
                                    :type           :host-path})
```

---

### execute-command!

Executes a command in the running container, and returns the result

#### Config parameters:

| Key                | Type                           | Description                                        |
| -------------      | :-------------                 | :-----                                             |
| First parameter:   |                                |                                                    |
| `container-config` | Map, mandatory                 | Return value of the `create` function              |
| Second parameter:  |                                |                                                    |
| `command`          | Vector with Strings, mandatory | A vector containing the command and its parameters |

#### Result:

| Key           | Type           | Description                       |
| ------------- | :------------- | :-----                            |
| `:exit-code`  | int            | Exit code of the executed command |
| `:stdout`     | String         | Content of stdout                 |
| `:stdin`      | String         | Content of stdin                  |

#### Example:

```clojure
(execute-command! container ["tail" "/opt/test.sql"])
```

### create-network

Creates a network. The optional map accepts config values for enabling ipv6 and setting the driver

#### Config parameters:

| Key           | Type           | Description                                                |
| ------------- | :------------- | :-----                                                     |
| `:ipv6`       | boolean        | Should the network enable IPv6?                            |
| `:driver`     | String         | The network driver used by Docker, e.g. `bridge` or `host` |

#### Result:

| Key           | Type                                    | Description                   |
| ------------- | :-------------                          | :-----                        |
| `:network`    | `org.testcontainers.containers.Network` | The instance of the network   |
| `:name`       | String                                  | The name of the network       |
| `:ipv6`       | boolean                                 | Does the network enable IPv6? |
| `:driver`     | String                                  | The network driver used       |

#### Example:

```clojure
;;Create with config
(create-network {:ipv6   false
                 :driver "overlay"})

;;Create with default config
(create-network)
```

### with-network

Create and bind a network to *network* and run `test-fn`. The network is removed after the function executes.

#### Config parameters:

`with-network` takes an optional map of options, equivalent to `create-network`.


#### Example:

```clojure
;; Run tests within an ephemeral network
(use-fixtures :once (tc/with-network {:ipv6? true}))

(deftest test-network-loaded
  (is (some? tc/*network*)))
```

### perform-cleanup!

Stops and removes all containers which were created in the JVM, including the REPL session you are in. This is helpful,
if you are exploring functionality with containers in the REPL, and create lots of instances on the fly without stopping
them. Testcontainers will remove all containers upon JVM shutdown, but the REPL keeps the JVM alive for a long time.

#### Config parameters:

None

#### Result:

None

#### Example:

```clojure
(perform-cleanup!)
```

### dump-logs

Call on a started container. Provided logging was enabled for a container, returns the given log presentation, e.g. as a
string

| Key                | Type           | Description                                        |
| ------------- | :-------------                          | :-----                         |
| `container-config` | Map, mandatory | The configuration describing the container for which the log should be retrieved |

## License

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
