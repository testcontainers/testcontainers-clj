(defproject clj-test-containers "0.4.0-SNAPSHOT"
  :description "A lightweight, unofficial wrapper around the Testcontainers Java library"

  :url "https://github.com/javahippie/clj-test-containers"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.testcontainers/testcontainers "1.15.0"]]

  :aliases {"test" ["run" "-m" "kaocha.runner"]
            "cljstyle" ["run" "-m" "cljstyle.main"]}

  :plugins [[jainsahab/lein-githooks "1.0.0"]]

  :profiles {:dev {:dependencies [[expound "0.8.5"]
                                  [lambdaisland/kaocha "1.0.641"]
                                  [lambdaisland/kaocha-cloverage "1.0-45"]
                                  [lambdaisland/kaocha-junit-xml "0.0.76"]
                                  [lambdaisland/kaocha-junit-xml "0.0.76"]
                                  [mvxcvi/cljstyle "0.13.0" :exclusions [org.clojure/clojure]]
                                  [org.clojure/test.check "1.1.0"]
                                  [org.clojure/tools.namespace "1.0.0"]
                                  [org.testcontainers/postgresql "1.15.0-rc2"]]
                   :source-paths ["dev-src"]}}

  :target-path "target/%s")
