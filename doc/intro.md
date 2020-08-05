# Introduction

## Who is this library for?
This library is meant for Clojure developers who want to write integration tests without having to worry about the infrastructure of the application.

## What are Testcontainers?
Depending on the complexity of your application, setting up the infrastructure for integration tests is not a simple task. Even if you only need a single database for the integration tests, you need to make it available to every system that executes the tests. But often, one database is not enough and you need to integrate with Webservices, Message Queues, Search Indexes, Caches… Testcontainers try to solve this problem: Very simply put, the testcontainers Java library provides an interface to interact with Docker and enables developers to easily bring up Docker containers for executing tests, and tearing them down again, afterwards. See more at [https://www.testcontainers.org/](https://www.testcontainers.org/)

## Why do we need a Clojure wrapper?
As Testcontainers is a Java library, we do not *need* a Clojure wrapper to work with it. It is completely possible to use it directly via Java interop code:

```java
(-> (org.testcontainers.containers.GenericContainer. "postgres:12.2")
                       (.withExposedPorts (into-array Integer [(int 5432)]))
                       (.withEnv "POSTGRES_PASSSWORD" pw)
                       (.start))
                       
```

But doing so is quite wordy and requires developers to use a lot of methods that manipulate a java instance. 

```clojure
(-> (tc/create {:image-name "postgres:12.1" 
                :exposed-ports [5432] 
                :env-vars {"POSTGRES_PASSWORD" pw}})
     tc/start!) 
```
