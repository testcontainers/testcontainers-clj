(ns clj-test-containers.core-test
  (:require [clojure.test :refer :all]
            [clj-test-containers.core :refer :all]))

(deftest init-test
  (testing "Testing basic testcontainer generic image initialisation"
    (let [container (create {:image-name "postgres:12.2"
                             :exposed-ports [5432] 
                             :env-vars {"POSTGRES_PASSWORD" "pw"}})
          initialized-container (start container)
          container-id (:id initialized-container)
          mapped-ports (:mapped-ports initialized-container)]
      (is (some? container-id))
      (is (some? mapped-ports))
      (is (some? (get mapped-ports 5432)))
      (stop container))))
