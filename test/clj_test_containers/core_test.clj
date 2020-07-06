(ns clj-test-containers.core-test
  (:require [clojure.test :refer :all]
            [clj-test-containers.core :refer :all]))

(deftest init-test
  (testing "Testing basic testcontainer generic image initialisation"
    (let [container (create {:image-name "postgres:12.2"
                             :exposed-ports [5432]
                             :env-vars {"POSTGRES_PASSWORD" "pw"}})
          initialized-container (start! container)
          stopped-container (stop! container)]
      (is (some? (:id initialized-container)))
      (is (some? (:mapped-ports initialized-container)))
      (is (some? (get (:mapped-ports initialized-container) 5432)))
      (is (nil? (:id stopped-container)))
      (is (nil? (:mapped-ports stopped-container))))))

(deftest execute-command-in-container

  (testing "Executing a command in the running Docker container"
    (let [container (create {:image-name "postgres:12.2"
                             :exposed-ports [5432]
                             :env-vars {"POSTGRES_PASSWORD" "pw"}})
          initialized-container (start! container)
          result (execute-command! initialized-container ["whoami"])
          stopped-container (stop! container)]
      (is (= 0 (:exit-code result)))
      (is (= "root\n" (:stdout result))))))

(deftest init-volume-test

  (testing "Testing mapping of a classpath resource"
    (let [container (-> (create {:image-name "postgres:12.2"
                                 :exposed-ports [5432]
                                 :env-vars {"POSTGRES_PASSWORD" "pw"}})
                        (map-classpath-resource! {:resource-path "test.sql"
                                                  :container-path "/opt/test.sql"
                                                  :mode :read-only}))
          initialized-container (start! container)
          file-check (execute-command! initialized-container ["tail" "/opt/test.sql"])
          stopped-container (stop! container)]
      (is (some? (:id initialized-container)))
      (is (some? (:mapped-ports initialized-container)))
      (is (some? (get (:mapped-ports initialized-container) 5432)))
      (is (= 0 (:exit-code file-check)))
      (is (nil? (:id stopped-container)))
      (is (nil? (:mapped-ports stopped-container)))))

  (testing "Testing mapping of a filesystem-binding"
    (let [container (-> (create {:image-name "postgres:12.2"
                                 :exposed-ports [5432]
                                 :env-vars {"POSTGRES_PASSWORD" "pw"}})
                        (bind-filesystem!  {:host-path "."
                                            :container-path "/opt"
                                            :mode :read-only}))
          initialized-container (start! container)
          file-check (execute-command! initialized-container ["tail" "/opt/README.md"])
          stopped-container (stop! container)]
      (is (some? (:id initialized-container)))
      (is (some? (:mapped-ports initialized-container)))
      (is (some? (get (:mapped-ports initialized-container) 5432)))
      (is (= 0 (:exit-code file-check)))
      (is (nil? (:id stopped-container)))
      (is (nil? (:mapped-ports stopped-container)))))

  (testing "Copying a file from the host into the container"
    (let [container (-> (create {:image-name "postgres:12.2"
                                 :exposed-ports [5432]
                                 :env-vars {"POSTGRES_PASSWORD" "pw"}})
                        (copy-file-to-container!  {:path "test.sql"
                                                   :container-path "/opt/test.sql"
                                                   :type :host-path}))
          initialized-container (start! container)
          file-check (execute-command! initialized-container ["tail" "/opt/test.sql"])
          stopped-container (stop! container)]
      (is (some? (:id initialized-container)))
      (is (some? (:mapped-ports initialized-container)))
      (is (some? (get (:mapped-ports initialized-container) 5432)))
      (is (= 0 (:exit-code file-check)))
      (is (nil? (:id stopped-container)))
      (is (nil? (:mapped-ports stopped-container)))))

  (testing "Copying a file from the classpath into the container"
    (let [container (-> (create {:image-name "postgres:12.2"
                                 :exposed-ports [5432]
                                 :env-vars {"POSTGRES_PASSWORD" "pw"}})
                        (copy-file-to-container!  {:path "test.sql"
                                                   :container-path "/opt/test.sql"
                                                   :type :classpath-resource}))
          initialized-container (start! container)
          file-check (execute-command! initialized-container ["tail" "/opt/test.sql"])
          stopped-container (stop! container)]
      (is (some? (:id initialized-container)))
      (is (some? (:mapped-ports initialized-container)))
      (is (some? (get (:mapped-ports initialized-container) 5432)))
      (is (= 0 (:exit-code file-check)))
      (is (nil? (:id stopped-container)))
      (is (nil? (:mapped-ports stopped-container))))))
