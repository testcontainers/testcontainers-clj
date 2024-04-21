(ns testcontainers-clj.spec.container
  (:require
   [clojure.spec.alpha :as s]
   [clojure.spec.gen.alpha :as gen])
  (:import
   (org.testcontainers.containers
    GenericContainer)))

(s/def ::container
  (s/with-gen #(instance? GenericContainer %)
              #(gen/fmap (fn [^String image-name] (GenericContainer. image-name))
                         (gen/string-alphanumeric))))

(s/def ::exposed-ports
  (s/coll-of (s/int-in 1 65535)))

(s/def ::reuse
  boolean?)

(s/def ::env-vars
  (s/map-of string? string?))

(s/def ::command
  (s/coll-of string?))

(s/def ::network-aliases
  (s/coll-of string?))

(s/def ::image-name
  string?)

(s/def ::http
  keyword?)

(s/def ::health
  keyword?)

(s/def ::log
  keyword?)

(s/def ::wait-strategy #{:http :health :port :log})

(s/def ::log-strategy #{:string})

(s/def ::path
  string?)

(s/def ::message
  string?)

(s/def ::check
  boolean?)

(s/def ::string
  string?)
