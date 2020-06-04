# clj-test-containers

[![javahippie](https://circleci.com/gh/javahippie/clj-test-containers.svg?style=svg)](<LINK>)

## What it is
This application is supposed to be a lightweight wrapper around the Testcontainers Java library. 

## What it isn't
This library does not provide tools to include testcontainers in your testing lifecycle. As there are many different test tools with different approaches to testing in the clojure world, handling the lifecycle is up to you.


## Usage
```clojure
(require '[clj-test-containers.core :as tc])

(def container (tc/create {:image-name "postgres:12.1" 
                          :exposed-ports [5432] 
                          :env-vars {"POSTGRES_PASSWORD" "verysecret"}}))

(tc/start postgres)

(tc/stop postgres)
```

## License

Copyright © 2020 Tim Zöller

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
