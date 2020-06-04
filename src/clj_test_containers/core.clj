(ns clj-test-containers.core)

(defn create
  "Sets the properties for a testcontainer instance"
  [{image-name :image-name
    exposed-ports :exposed-ports
    env-vars :env-vars
    command :command}]

  (let [container (org.testcontainers.containers.GenericContainer. image-name)]
    (.setExposedPorts container (map int exposed-ports))
    
    (doseq [pair env-vars]
      (.addEnv container (first pair) (second pair)))

    (if (some? command)
      (.setCommand container command))
    
    {:container container
     :exposed-ports (.getExposedPorts container)
     :env-vars (.getEnvMap container)
     :host (.getHost container)}))

(defn start 
  "Starts the underlying testcontainer instance and adds new values to the response map, e.g. :id and :first-mapped-port"
  [container-conf]
  (let [container (:container container-conf)]
    (.start container)
    (-> container-conf
        (assoc :id (.getContainerId container))
        (assoc :mapped-ports (into {} (map (fn [port] [port 
                                                       (.getMappedPort container port)]) 
                                           (:exposed-ports container-conf)))))))

(defn stop 
  "Stops the underlying container"
  [container-conf]
  (.stop (:container container-conf))
  (-> container-conf
      (dissoc :id)
      (dissoc :mapped-ports)))
