(ns testcontainers-clj.spec.network
  (:require
   [clojure.spec.alpha :as s])
  (:import
   (org.testcontainers.containers
    Network)))

(s/def ::id
  string?)

(s/def ::ipv6
  (s/nilable boolean?))

(s/def ::driver
  (s/nilable string?))

(s/def ::name
  string?)

(s/def ::network
  (s/with-gen #(instance? Network %)
              #(s/gen #{(Network/newNetwork)})))
