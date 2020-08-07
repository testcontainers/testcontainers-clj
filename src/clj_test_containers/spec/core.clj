(ns clj-test-containers.spec.core
  (:require
   [clj-test-containers.spec.container :as csc]
   [clj-test-containers.spec.network :as csn]
   [clojure.spec.alpha :as s]))

(s/def ::network
  (s/nilable (s/keys :req-un [::csn/network
                              ::csn/id
                              ::csn/name
                              ::csn/ipv6
                              ::csn/driver])))

(s/def ::container
  (s/keys :req-un [::csc/container
                   ::csc/exposed-ports
                   ::csc/env-vars
                   ::csc/host]
          :opt-un [::network]))

(s/def ::init-options
  (s/keys :req-un [::csc/container]
          :opt-un [::csc/exposed-ports
                   ::csc/env-vars
                   ::csc/command
                   ::network
                   ::csc/network-aliases]))

(s/def ::create-options
  (s/keys :req-un [::csc/image-name]
          :opt-un [::csc/exposed-ports
                   ::csc/env-vars
                   ::csc/command
                   ::network
                   ::csc/network-aliases]))

(s/def ::init-network-options
  (s/keys :opt-un [::csn/ipv6
                   ::csn/driver]))
