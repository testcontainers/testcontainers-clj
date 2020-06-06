(ns clj-test-containers.core-test
  (:require [clojure.test :refer :all]
            [clj-test-containers.core :refer :all]))

(deftest init-test
  (testing "Testing basic testcontainer generic image initialisation"
    (let [container (create {:image-name "postgres:12.2"
                             :exposed-ports [5432] 
                             :env-vars {"POSTGRES_PASSWORD" "pw"}})
          initialized-container (start container)
          stopped-container (stop container)]
      (is (some? (:id initialized-container)))
      (is (some? (:mapped-ports initialized-container)))
      (is (some? (get (:mapped-ports initialized-container) 5432)))
      (is (nil? (:id stopped-container)))
      (is (nil? (:mapped-ports stopped-container))))))

(deftest init-with-volume-test

  (testing "Testing mapping of a classpath resource"
    (let [container (-> (create {:image-name "postgres:12.2"
                                 :exposed-ports [5432] 
                                 :env-vars {"POSTGRES_PASSWORD" "pw"}})
                        (configure-volume {:classpath-resource-mapping {:resource-path "test.sql"
                                                                        :container-path "/opt/test.sql"
                                                                        :mode :read-only}}))
          initialized-container (start container)
          stopped-container (stop container)]
      (is (some? (:id initialized-container)))
      (is (some? (:mapped-ports initialized-container)))
      (is (some? (get (:mapped-ports initialized-container) 5432)))
      (is (nil? (:id stopped-container)))
      (is (nil? (:mapped-ports stopped-container)))))

  (testing "Testing mapping of a filesystem-binding"
    (let [container (-> (create {:image-name "postgres:12.2"
                                 :exposed-ports [5432] 
                                 :env-vars {"POSTGRES_PASSWORD" "pw"}})
                        (configure-volume {:file-system-bind {:host-path "/tmp"
                                                              :container-path "/opt"
                                                              :mode :read-only}}))
          initialized-container (start container)
          stopped-container (stop container)]
      (is (some? (:id initialized-container)))
      (is (some? (:mapped-ports initialized-container)))
      (is (some? (get (:mapped-ports initialized-container) 5432)))
      (is (nil? (:id stopped-container)))
      (is (nil? (:mapped-ports stopped-container)))))

  (testing "Copying a file from the host into the container"
    (let [container (-> (create {:image-name "postgres:12.2"
                                 :exposed-ports [5432] 
                                 :env-vars {"POSTGRES_PASSWORD" "pw"}})
                        (copy-file-to-container  {:path "test.sql"
                                                  :container-path "/opt"
                                                  :type :host-path}))
          initialized-container (start container)
          stopped-container (stop container)]
      (is (some? (:id initialized-container)))
      (is (some? (:mapped-ports initialized-container)))
      (is (some? (get (:mapped-ports initialized-container) 5432)))
      (is (nil? (:id stopped-container)))
      (is (nil? (:mapped-ports stopped-container)))))

  (testing "Copying a file from the classpath into the container"
    (let [container (-> (create {:image-name "postgres:12.2"
                                 :exposed-ports [5432] 
                                 :env-vars {"POSTGRES_PASSWORD" "pw"}})
                        (copy-file-to-container  {:path "test.sql"
                                                  :container-path "/opt"
                                                  :type :classpath-resource}))
          initialized-container (start container)
          stopped-container (stop container)]
      (is (some? (:id initialized-container)))
      (is (some? (:mapped-ports initialized-container)))
      (is (some? (get (:mapped-ports initialized-container) 5432)))
      (is (nil? (:id stopped-container)))
      (is (nil? (:mapped-ports stopped-container))))))
