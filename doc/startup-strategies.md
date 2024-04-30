# Startup strategies

Normally, test containers wait until the container has reached the running state before continuing with the test. In some scenarios, you may wish for a container to run to completion before proceeding with the test suite. This is what startup strategies are for.

See also: [Startup check strategies](https://java.testcontainers.org/features/startup_and_waits/#startup-check-strategies)

## Running Strategy (default)

The `:running` strategy waits for the container to enter a running state.
Example:

```clojure
{:startup {:strategy :running}}
```

## One-shot Strategy

The `:one-shot` strategy waits for the container to run to completion with exit status 0.

```clojure
{:startup {:strategy :one-shot}}
```
