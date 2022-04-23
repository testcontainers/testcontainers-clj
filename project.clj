(defproject clj-test-containers "0.7.0"
  :description "A lightweight, unofficial wrapper around the Testcontainers Java library"

  :url "https://github.com/javahippie/clj-test-containers"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.testcontainers/testcontainers "1.17.1"]]

  :aliases {"test" ["run" "-m" "kaocha.runner"]
            "cljstyle" ["run" "-m" "cljstyle.main"]}

  :plugins [[jainsahab/lein-githooks "1.0.0"]]

  :profiles {:dev {:dependencies [[expound "0.9.0"]
                                  [lambdaisland/kaocha "1.65.1029"]
                                  [lambdaisland/kaocha-cloverage "1.0.75"]
                                  [lambdaisland/kaocha-junit-xml "0.0.76"]
                                  [mvxcvi/cljstyle "0.15.0" :exclusions [org.clojure/clojure]]
                                  [org.clojure/test.check "1.1.1"]
                                  [orchestra "2021.01.01-1"]
                                  [org.clojure/tools.namespace "1.2.0"]
                                  [org.testcontainers/postgresql "1.16.3"]
                                  [com.fzakaria/slf4j-timbre "0.3.21"]
                                  [nrepl "0.9.0"]]
                   :source-paths ["dev-src"]}}

  :target-path "target/%s")
