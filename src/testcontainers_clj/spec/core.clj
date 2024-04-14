(ns testcontainers-clj.spec.core
  (:require
   [testcontainers-clj.spec.container :as csc]
   [testcontainers-clj.spec.network :as csn]
   [clojure.spec.alpha :as s]))

(s/def ::wait-for
  (s/keys :req-un [::csc/wait-strategy]
          :opt-un [::csc/path
                   ::csc/message
                   ::csc/check]))

(s/def ::log-to
  (s/keys :req-un [::csc/log-strategy]
          :opt-un [::csc/string]))

(s/def ::network
  (s/nilable (s/keys :req-un [::csn/network
                              ::csn/name
                              ::csn/ipv6
                              ::csn/driver])))

(s/def ::container
  (s/keys :req-un [::csc/container
                   ::csc/exposed-ports
                   ::csc/env-vars
                   ::csc/host]
          :opt-un [::csc/reuse
                   ::network
                   ::wait-for
                   ::log-to]))

(s/def ::init-options
  (s/keys :req-un [::csc/container]
          :opt-un [::csc/exposed-ports
                   ::csc/reuse
                   ::csc/env-vars
                   ::csc/command
                   ::network
                   ::wait-for
                   ::log-to
                   ::csc/network-aliases]))

(s/def ::create-options
  (s/keys :req-un [::csc/image-name]
          :opt-un [::csc/exposed-ports
                   ::csc/env-vars
                   ::csc/command
                   ::network
                   ::wait-for
                   ::log-to
                   ::csc/network-aliases]))

(s/def ::create-network-options
  (s/keys :opt-un [::csn/ipv6
                   ::csn/driver]))
