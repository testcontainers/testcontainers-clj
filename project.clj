(defproject clj-test-containers "0.1.0"
  :description "A lightweight, unofficial wrapper around the Testcontainers Java library"
  :url "https://github.com/javahippie/clj-test-containers"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.testcontainers/testcontainers "1.14.3"]]
  :plugins [[metosin/bat-test "0.4.4"]]
  :bat-test {:report [:pretty {:type :junit 
                               :output-to "target/junit.xml"}]}
  :target-path "target/%s")

