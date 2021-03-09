(ns clj-test-containers.core
  (:require
   [clj-test-containers.spec.core :as cs]
   [clojure.spec.alpha :as s]
   [clojure.string])
  (:import
   (java.nio.file
    Paths)
   (org.testcontainers.containers
    BindMode
    GenericContainer
    Network)
   (org.testcontainers.containers.output
    ToStringConsumer)
   (org.testcontainers.containers.wait.strategy
    Wait)
   (org.testcontainers.images.builder
    ImageFromDockerfile)
   (org.testcontainers.utility
    MountableFile
    ResourceReaper)))

(defn- resolve-bind-mode
  (^BindMode [bind-mode]
   (if (= :read-write bind-mode)
     BindMode/READ_WRITE
     BindMode/READ_ONLY)))

(defn- reaper-instance
  []
  (ResourceReaper/instance))

(defmulti wait
  "Sets a wait strategy to the container.  Supports :http, :health and :log as
  strategies.

  ## HTTP Strategy
  The :http strategy will only accept the container as initialized if it can be
  accessed via HTTP. It accepts a path, a port, a vector of status codes, a
  boolean that specifies if TLS is enabled, a read timeout in seconds and a map
  with basic credentials, containing username and password. Only the path is
  required, all others are optional.

  Example:

  ```clojure
  (wait {:wait-strategy :http
         :port 80
         :path \"/\"
         :status-codes [200 201]
         :tls true
         :read-timeout 5
         :basic-credentials {:username \"user\"
                             :password \"password\"}}
        container)
  ```

  ## Health Strategy
  The :health strategy only accepts a true or false value. This enables support
  for Docker's healthcheck feature, whereby you can directly leverage the
  healthy state of your container as your wait condition.

  Example:

  ```clojure
  (wait {:wait-strategy :health :true} container)
  ```

  ## Log Strategy
  The :log strategy accepts a message which simply causes the output of your
  container's log to be used in determining if the container is ready or not.
  The output is `grepped` against the log message.

  Example:

  ```clojure
  (wait {:wait-strategy :log
         :message \"accept connections\"} container)
  ```"
  :wait-strategy)

(defmethod wait :http
  [{:keys [path
           port
           status-codes
           tls
           read-timeout
           basic-credentials] :as options}
   ^GenericContainer container]
  (let [for-http (Wait/forHttp path)]
    (when port
      (.forPort for-http port))

    (doseq [status-code status-codes]
      (.forStatusCode for-http status-code))

    (when tls
      (.usingTls for-http))

    (when read-timeout
      (.withReadTimeout for-http (java.time.Duration/ofSeconds read-timeout)))

    (when basic-credentials
      (let [{username :username password :password} basic-credentials]
        (.withBasicCredentials for-http username password)))

    (.waitingFor container for-http)

    {:wait-for-http (dissoc options :strategy)}))

(defmethod wait :health
  [_ ^GenericContainer container]
  (.waitingFor container (Wait/forHealthcheck))
  {:wait-for-healthcheck true})

(defmethod wait :log
  [{:keys [message]} ^GenericContainer container]
  (let [log-message (str ".*" message ".*\\n")]
    (.waitingFor container (Wait/forLogMessage log-message 1))
    {:wait-for-log-message log-message}))

(defmethod wait :default [_ _] nil)

(s/fdef init
        :args (s/cat :init-options ::cs/init-options)
        :ret ::cs/container)

(defn init
  "Sets the properties for a testcontainer instance"
  [{:keys [^GenericContainer container
           exposed-ports
           env-vars
           command
           network
           network-aliases
           wait-for] :as init-options}]

  (.setExposedPorts container (map int exposed-ports))

  (doseq [[k v] env-vars]
    (.addEnv container k v))

  (when command
    (.setCommand container ^"[Ljava.lang.String;" (into-array String command)))

  (when network
    (.setNetwork container (:network network)))

  (when network-aliases
    (.setNetworkAliases container network-aliases))

  (merge init-options {:container container
                       :exposed-ports (vec (.getExposedPorts container))
                       :env-vars (into {} (.getEnvMap container))
                       :host (.getHost container)
                       :network network} (wait wait-for container)))

(s/fdef create
        :args (s/cat :create-options ::cs/create-options)
        :ret ::cs/container)

(defn create
  "Creates a generic testcontainer and sets its properties"
  [{:keys [image-name] :as options}]
  (->> (GenericContainer. ^String image-name)
       (assoc options :container)
       init))

(defn create-from-docker-file
  "Creates a testcontainer from a provided Dockerfile"
  [{:keys [docker-file] :as options}]
  (->> (.withDockerfile (ImageFromDockerfile.)
                        (Paths/get "." (into-array [docker-file])))
       (GenericContainer.)
       (assoc options :container)
       init))

(defn map-classpath-resource!
  "Maps a resource in the classpath to the given container path. Should be
  called before starting the container!"
  [{:keys [^GenericContainer container] :as container-config}
   {:keys [^String resource-path ^String container-path mode]}]
  (assoc container-config
         :container
         (.withClasspathResourceMapping container
                                        resource-path
                                        container-path
                                        (resolve-bind-mode mode))))

(defn bind-filesystem!
  "Binds a source from the filesystem to the given container path. Should be
  called before starting the container!"
  [{:keys [^GenericContainer container] :as container-config}
   {:keys [^String host-path ^String container-path mode]}]
  (assoc container-config
         :container
         (.withFileSystemBind container
                              host-path
                              container-path
                              (resolve-bind-mode mode))))

(defn copy-file-to-container!
  "If a container is not yet started, adds a mapping from mountable file to
  container path that will be copied to the container on startup. If the
  container is already running, copy the file to the running container."
  [{:keys [^GenericContainer container id] :as container-config}
   {:keys [^String container-path ^String path type]}]
  (let [^MountableFile mountable-file
        (case type
          :classpath-resource (MountableFile/forClasspathResource path)
          :host-path          (MountableFile/forHostPath path))]
    (if id
      (do
        (.copyFileToContainer container mountable-file container-path)
        container-config)
      (assoc container-config
             :container
             (.withCopyFileToContainer container
                                       mountable-file
                                       container-path)))))

(defn execute-command!
  "Executes a command in the container, and returns the result"
  [{:keys [^GenericContainer container]} command]
  (let [result (.execInContainer container (into-array command))]
    {:exit-code (.getExitCode result)
     :stdout    (.getStdout result)
     :stderr    (.getStderr result)}))

(defmulti log
  "Sets a log strategy on the container as a means of accessing the container
  logs.  It currently only supports a :string as the strategy to use.

  ## String Strategy
  The :string strategy sets up a function in the returned map, under the
  `string-log` key. This function enables the dumping of the logs when passed to
  the `dump-logs` function.

  Example:

  ```clojure
  {:log-strategy :string}
  ```

  Then, later in your program, you can access the logs thus:

  ```clojure
  (def container-config (tc/start! container))
  (tc/dump-logs container-config)
  ```
   "
  :log-strategy)

(defmethod log :string
  [_ ^GenericContainer container]
  (let [to-string-consumer (ToStringConsumer.)]
    (.followOutput container to-string-consumer)
    {:log (fn []
            (-> (.toUtf8String to-string-consumer)
                (clojure.string/replace #"\n+" "\n")))}))

(defmethod log :slf4j [_ _] nil) ;; Not yet implemented

(defmethod log :default [_ _] nil) ;; Not yet implemented

(defn dump-logs
  "Dumps the logs found by invoking the function on the :string-log key"
  [container-config]
  ((:log container-config)))

(defn start!
  "Starts the underlying testcontainer instance and adds new values to the
  response map, e.g. :id and :first-mapped-port"
  [{:keys [^GenericContainer container
           log-to
           exposed-ports] :as container-config}]
  (.start container)
  (let [map-port (fn map-port
                   [port]
                   [port (.getMappedPort container port)])
        mapped-ports (into {} (map map-port) exposed-ports)
        container-id ^String (.getContainerId container)
        image-name ^String (.getDockerImageName container)
        logger (log log-to container)]
    (.registerContainerForCleanup ^ResourceReaper (reaper-instance)
                                  container-id
                                  image-name)
    (-> container-config
        (merge {:id container-id
                :mapped-ports mapped-ports
                :image-name image-name} logger)
        (dissoc :log-to))))

(defn stop!
  "Stops the underlying container"
  [{:keys [^GenericContainer container] :as container-config}]
  (.stop container)
  (dissoc container-config :id :string-log :mapped-ports))

(s/fdef create-network
        :args (s/alt :nullary (s/cat)
                     :unary (s/cat :create-network-options
                                   ::cs/create-network-options))
        :ret ::cs/network)

(defn create-network
  "Creates a network. The optional map accepts config values for enabling ipv6
  and setting the driver"
  ([]
   (create-network {}))
  ([{:keys [ipv6 driver]}]
   (let [builder (Network/builder)]
     (when ipv6
       (.enableIpv6 builder true))

     (when driver
       (.driver builder driver))

     (let [network (.build builder)
           network-name (.getName network)]
       (.registerNetworkIdForCleanup ^ResourceReaper (reaper-instance) network-name)
       {:network network
        :name network-name
        :ipv6 (.getEnableIpv6 network)
        :driver (.getDriver network)}))))

(def ^:deprecated init-network create-network)

(defn perform-cleanup!
  "Stops and removes all container instances which were created in the active JVM or REPL session"
  []
  (.performCleanup ^ResourceReaper (reaper-instance)))


;;; REPL Helpers
(comment
  (start! (create {:image-name "postgres:12.1"}))
  (perform-cleanup!))
