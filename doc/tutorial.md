# Tutorial

The list of functions included in the library until now is as following:

* `create`: Creates a new Testcontainers instance, accepts parameters for mapped ports, environment variables and a
  start command
* `map-classpath-resource!`: Maps a resource from your classpath into the containers file system
* `bind-filesystem!`: Binds a path from your local filesystem into the Docker container as a volume
* `start!`: Starts the container
* `stop!`: Stops the container
* `copy-file-to-container!`: Copies a file from your filesystem or classpath into the running container
* `execute-command!`: Executes a command in the running container, and returns the result

The functions accept and return a map structure, which enables us to operate them on the same data structure in a
consistent way. The example shown with Java Interop above would look like this, when using the wrapped functions:

```clojure
(require '[testcontainers-clj.core :as tc])

(deftest db-integration-test
         (testing "A simple PostgreSQL integration test"
                  (let [pw "db-pass"
                        postgres (-> (tc/create {:image-name    "postgres:12.1"
                                                 :exposed-ports [5432]
                                                 :env-vars      {"POSTGRES_PASSWORD" pw}}))]
                    (tc/start! postgres)
                    (let [datasource (jdbc/get-datasource {:dbtype   "postgresql"
                                                           :dbname   "postgres"
                                                           :user     "postgres"
                                                           :password pw
                                                           :host     (:host postgres)
                                                           :port     (get (:mapped-ports container) 5432)})]
                      (is (= [{:one 1 :two 2}] (with-open [connection (jdbc/get-connection datasource)]
                                                 (jdbc/execute! connection ["SELECT 1 ONE, 2 TWO"])))))
                    (tc/stop! postgres))))
```

## Executing commands inside the container

The `execute-command` function enables us to run commands inside the container. The function accepts a container and a
vector of strings as parameters, with the first string being the command, followed by potential parameters. The function
returns a map with an `:exit-code`, `:stdout` and `:stderr`:

```clojure
(execute-command! container ["whoami"])

> {:exit-code 0
   :stdout    "root"}
```

## Mounting files into the container

For some test scenarios it can be helpful to mount files from your filesystem or the resource path of your application
into the container, before it is started. This could be helpful if you want to load a dumpfile into your database,
before executing the tests. You can do this with the functions `map-classpath-resource!` and `bind-filesystem!`:

```clojure
(map-classpath-resource! container
                         {:resource-path  "test.sql"
                          :container-path "/opt/test.sql"
                          :mode           :read-only})
```  

```clojure
(bind-filesystem! {:host-path      "."
                   :container-path "/opt"
                   :mode           :read-only})
```

It is also possible to copy files into a running container instance:

```clojure
(copy-file-to-container! {:path           "test.sql"
                          :container-path "/opt/test.sql"
                          :type           :host-path})
```

# Fixtures for Clojure Test

The above example creates a Testcontainers instance in the test function itself. If we did this for all of our
integration tests, this would spin up a docker image for every test function, and tear it down again, afterwards. If we
want to create one image for all tests in the same namespace, we can use
Clojures [`use-fixtures`](https://clojuredocs.org/clojure.test/use-fixtures) function, which is described like this:

> Wrap test runs in a fixture function to perform setup and teardown. Using a fixture-type of :each wraps every test individually, while :once wraps the whole run in a single function.

Assuming we have a function `initialize-db!` in our application which sets up a JDBC connection and stores it in an
atom, a fixture for Testcontainers could look like this:

```clojure
(use-fixtures :once (fn [f]
                      (let [pw       "apassword"
                            postgres (tc/start! (tc/create {:image-name    "postgres:12.2"
                                                            :exposed-ports [5432]
                                                            :env-vars      {"POSTGRES_PASSWORD" pw}}))]
                        (my-app/initialize-db! {:dbtype   "postgresql"
                                                :dbname   "postgres"
                                                :user     "postgres"
                                                :password pw
                                                :host     (:host postgres)
                                                :port     (get (:mapped-ports postgres) 5432)}))
                      (f)
                      (tc/stop! postgres)))
```

This will set up the container, execute all test functions in the namespace and stop the container afterwards.
