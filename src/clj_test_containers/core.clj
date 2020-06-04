(ns clj-test-containers.core)

(defn init 
  "Sets the properties for a testcontainer instance"
  [{image-name :image-name
    exposed-ports :exposed-ports
    env-vars :env-vars
    command :command
    fs-bind :file-system-bind
    waiting-for :waiting-for
    copy-file :copy-file-to-container
    extra-host :extra-host
    network :network
    network-aliases :network-aliases
    network-mode :network-mode
    image-pull-policy :image-pull-policy
    classpath-resource-mapping :classpath-resource-mapping
    startup-timeout :startup-timeout
    privileged-mode :privileged-mode
    startup-check-strategy :startup-check-strategy
    working-directory :working-directory
    follow-ouptut :follow-output
    log-consumer :log-consumer}]
  (let [container (-> (org.testcontainers.containers.GenericContainer. image-name)
                      (.withExposedPorts (into-array Integer (map int exposed-ports)))
                      (.withEnv env-vars)
                      (as-> container
                          (if (some? command)
                            (.withCommand command)
                            container)))]
    {:container container
     :exposed-ports (.getExposedPorts container)
     :env-vars (.getEnvMap container)
     :host (.getHost container)}))
