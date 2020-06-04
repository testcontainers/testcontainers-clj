(def project 'clj-test-containers)
(def version "0.1.0-SNAPSHOT")

(set-env! :resource-paths #{"resources" "src"}
          :source-paths   #{"test"}
          :dependencies   '[[org.clojure/clojure "1.10.1"]
                            [org.testcontainers/testcontainers "1.14.3"]
                            [metosin/bat-test "0.4.4" :scope "test"]])

(task-options!
 pom {:project     project
      :version     version
      :description "A lightweight, unofficial wrapper around the Testcontainers Java library"
      :url         "https://github.com/javahippie/clj-test-containers"
      :scm         {:url "https://github.com/javahippie/clj-test-containers"}
      :license     {"Eclipse Public License"
                    "http://www.eclipse.org/legal/epl-v10.html"}})

(deftask build
  "Build and install the project locally."
  []
  (comp (pom) (jar) (install)))

(require '[metosin.bat-test :refer (bat-test)])
