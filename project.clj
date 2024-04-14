(defproject org.testcontainers/testcontainers-clj "unspecified"
  :description "A lightweight, official wrapper around the Testcontainers Java library"

  :url "https://github.com/testcontainers/testcontainers-clj"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.testcontainers/testcontainers "1.19.7"]]

  :aliases {"test" ["run" "-m" "kaocha.runner"]
            "cljstyle" ["run" "-m" "cljstyle.main"]}

  :plugins [[jainsahab/lein-githooks "1.0.0"]]

  :profiles {:dev {:dependencies [[expound "0.9.0"]
                                  [lambdaisland/kaocha "1.88.1376"]
                                  [lambdaisland/kaocha-cloverage "1.1.89"]
                                  [lambdaisland/kaocha-junit-xml "1.17.101"]
                                  [mvxcvi/cljstyle "0.16.630" :exclusions [org.clojure/clojure]]
                                  [org.clojure/test.check "1.1.1"]
                                  [orchestra "2021.01.01-1"]
                                  [org.clojure/tools.namespace "1.5.0"]
                                  [org.testcontainers/postgresql "1.19.7"]
                                  [com.fzakaria/slf4j-timbre "0.4.1"]
                                  [nrepl "1.0.0"]]
                   :source-paths ["dev-src"]}
             :release {:deploy-repositories [["maven" {:url           "https://oss.sonatype.org/service/local/staging/deploy/maven2"
                                                       :username      :env/ossrh_username
                                                       :password      :env/ossrh_password
                                                       :sign-releases false}]]}}

  :target-path "target/%s")
