(defproject clj-test-containers "0.2.0"
  :description "A lightweight, unofficial wrapper around the Testcontainers Java library"

  :url "https://github.com/javahippie/clj-test-containers"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.testcontainers/testcontainers "1.14.3"]]

  :aliases {"test" ["run" "-m" "kaocha.runner"]}

  :profiles {:dev {:dependencies [[org.testcontainers/postgresql "1.14.3"]
                                  [lambdaisland/kaocha-cloverage "1.0-45"]
                                  [lambdaisland/kaocha "1.0.641"]
                                  [lambdaisland/kaocha-junit-xml "0.0.76"]]}}

  :target-path "target/%s")

