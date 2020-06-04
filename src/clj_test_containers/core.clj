5(ns clj-test-containers.core)

(defn- resolve-bind-mode [bind-mode]
  (if (= :read-write bind-mode)
    org.testcontainers.containers.BindMode/READ_WRITE
    org.testcontainers.containers.BindMode/READ_ONLY))

(defn- configure-volume [container {classpath-resource-mapping :classpath-resource-mapping
                                    file-system-bind :file-system-bind}]

  (if (some? classpath-resource-mapping)
    (let [{resource-path :resource-path
           container-path :container-path
           mode :mode} classpath-resource-mapping]
      (.withClasspathResourceMapping container 
                                     resource-path 
                                     container-path 
                                     (resolve-bind-mode mode))))

  (if (some? file-system-bind)
    (let [{host-path :host-path
           container-path :container-path
           mode :mode} file-system-bind]
      (.withFileSystemBind container 
                           host-path
                           container-path))))

(defn create
  "Sets the properties for a testcontainer instance"
  [{image-name :image-name
    exposed-ports :exposed-ports
    env-vars :env-vars
    command :command
    volume :volume}]

  (let [container (org.testcontainers.containers.GenericContainer. image-name)]
    (.setExposedPorts container (map int exposed-ports))
    
    (if (some? env-vars)
      (doseq [pair env-vars]
        (.addEnv container (first pair) (second pair))))

    (if (some? command)
      (.setCommand container command))
    
    (if (some? volume)
      (configure-volume container volume))

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
