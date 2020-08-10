(ns clj-test-containers.spec.container
  (:require
   [clojure.spec.alpha :as s]
   [clojure.spec.gen.alpha :as gen])
  (:import
   (org.testcontainers.containers
    GenericContainer)))

(s/def ::container
  (s/with-gen #(instance? GenericContainer %)
              #(gen/fmap (fn [image-name] (GenericContainer. image-name))
                         (gen/string-alphanumeric))))

(s/def ::exposed-ports
  (s/coll-of (s/int-in 1 65535)))

(s/def ::env-vars
  (s/map-of string? string?))

(s/def ::command
  (s/coll-of string?))

(s/def ::network-aliases
  (s/coll-of string?))

(s/def ::image-name
  string?)
