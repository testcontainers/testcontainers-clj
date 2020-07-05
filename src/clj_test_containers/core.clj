(ns clj-test-containers.core
  (:import [org.testcontainers.containers
            GenericContainer]
           [org.testcontainers.utility
            MountableFile]
           [org.testcontainers.containers BindMode]))

(defn- resolve-bind-mode
  [bind-mode]
  (if (= :read-write bind-mode)
    BindMode/READ_WRITE
    BindMode/READ_ONLY))

(defn create
  "Sets the properties for a testcontainer instance"
  [{:keys [image-name exposed-ports env-vars command]}]
  (let [container (GenericContainer. image-name)]
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
   {:keys [resource-path container-path mode]}]
  (assoc container-config :container (.withClasspathResourceMapping (:container container-config)
                                                                    resource-path
                                                                    container-path
                                                                    (resolve-bind-mode mode))))

(defn bind-filesystem!
  "Binds a source from the filesystem to the given container path. Should be called before starting the container!"
  [container-config {:keys [host-path container-path mode]}]
  (assoc container-config
         :container (.withFileSystemBind (:container container-config)
                                         host-path
                                         container-path
                                         (resolve-bind-mode mode))))

(defn copy-file-to-container!
  "Copies a file into the running container"
  [container-config
   {:keys [container-path path type]}]
  (let [mountable-file (cond
                         (= :classpath-resource type)
                         (MountableFile/forClasspathResource path)

                         (= :host-path type)
                         (MountableFile/forHostPath path)
                         :else
                         :error)]
    (assoc container-config
           :container
           (.withCopyFileToContainer (:container container-config)
                                     mountable-file
                                     container-path))))

(defn execute-command!
  "Executes a command in the container, and returns the result"
  [container-config command]
  (let [container (:container container-config)
        result (.execInContainer container
                                 (into-array command))]
    {:exit-code (.getExitCode result)
     :stdout (.getStdout result)
     :stderr (.getStderr result)}))

(defn start!
  "Starts the underlying testcontainer instance and adds new values to the response map, e.g. :id and :first-mapped-port"
  [container-config]
  (let [container (:container container-config)]
    (.start container)
    (-> container-config
        (assoc :id (.getContainerId container))
        (assoc :mapped-ports (into {}
                                   (map (fn [port] [port (.getMappedPort container port)])
                                        (:exposed-ports container-config)))))))

(defn stop!
  "Stops the underlying container"
  [container-config]
  (.stop (:container container-config))
  (-> container-config
      (dissoc :id)
      (dissoc :mapped-ports)))
