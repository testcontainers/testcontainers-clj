(ns clj-test-containers.core)

(defn- resolve-bind-mode [bind-mode]
  (if (= :read-write bind-mode)
    org.testcontainers.containers.BindMode/READ_WRITE
    org.testcontainers.containers.BindMode/READ_ONLY))

(defn create
  "Sets the properties for a testcontainer instance"
  [{image-name :image-name
    exposed-ports :exposed-ports
    env-vars :env-vars
    command :command}]

  (let [container (org.testcontainers.containers.GenericContainer. image-name)]
    (.setExposedPorts container (map int exposed-ports))
    
    
    (if (some? env-vars)
      (doseq [pair env-vars]
        (.addEnv container (first pair) (second pair))))

    (if (some? command)
      (.setCommand container command))
    
    {:container container
     :exposed-ports (.getExposedPorts container)
     :env-vars (.getEnvMap container)
     :host (.getHost container)}))


(defn map-classpath-resource! 
  "Maps a resource in the classpath to the given container path. Should be called before starting the container!"
  [container-config 
   {resource-path :resource-path
    container-path :container-path
    mode :mode}]
  
  (assoc container-config :container (.withClasspathResourceMapping (:container container-config) 
                                                                    resource-path 
                                                                    container-path 
                                                                    (resolve-bind-mode mode))))

(defn bind-filesystem! 
  "Binds a source from the filesystem to the given container path. Should be called before starting the container!"
  [container-config 
   {host-path :host-path
    container-path :container-path
    mode :mode}]
  
  (assoc container-config :container (.withFileSystemBind (:container container-config)  
                                                          host-path
                                                          container-path
                                                          (resolve-bind-mode mode))))

(defn copy-file-to-container!
  "Copies a file into the running container"
  [container-conf {container-path :container-path
                   path :path
                   type :type}]

  (let [mountable-file (cond 
                         (= :classpath-resource type) (org.testcontainers.utility.MountableFile/forClasspathResource path)
                         (= :host-path type) (org.testcontainers.utility.MountableFile/forHostPath path)
                         :else :error)]
    (assoc container-conf 
           :container 
           (.withCopyFileToContainer (:container container-conf) 
                                     mountable-file 
                                     container-path))))

(defn execute-command! 
  "Executes a command in the container, and returns the result"
  [container-conf command]
  (let [container (:container container-conf)
        result (.execInContainer container
                    (into-array command))]
    {:exit-code (.getExitCode result)
     :stdout (.getStdout result)
     :stderr (.getStderr result)}))

(defn start! 
  "Starts the underlying testcontainer instance and adds new values to the response map, e.g. :id and :first-mapped-port"
  [container-conf]
  (let [container (:container container-conf)]
    (.start container)
    (-> container-conf
        (assoc :id (.getContainerId container))
        (assoc :mapped-ports (into {} (map (fn [port] [port 
                                                       (.getMappedPort container port)]) 
                                           (:exposed-ports container-conf)))))))

(defn stop!
  "Stops the underlying container"
  [container-conf]
  (.stop (:container container-conf))
  (-> container-conf
      (dissoc :id)
      (dissoc :mapped-ports)))


