# Log strategies

This library offers two ways to access the logs of the running container: The :string strategy and the :fn strategy.

## String Strategy

The `:string` strategy sets up a function in the returned map, under the `string-log` key. This function enables the
dumping of the logs when passed to the `dump-logs` function.

Example:

```clojure
{:log-strategy :string}
```

Then, later in your program, you can access the logs thus:

```clojure
(def container-config (tc/start! container))
(tc/dump-logs container-config)
```

## Function Strategy

The `:fn` strategy accepts an additional parameter `:function` in the configuration map, which allows you to pass a
function to the Testcontainers log mechanism which accepts a single String parameter and gets called for every log line.
This way you can pass the container logging on to the logging library of your choice.

Example:

```clojure
{:log-strategy :fn
 :function     (fn [log-line] (println "From Container: " log-line)}
```