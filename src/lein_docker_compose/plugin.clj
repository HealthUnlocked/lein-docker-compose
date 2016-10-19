(ns lein-docker-compose.plugin
  (:use [robert.hooke :only (add-hook)])
  (:require [yaml.core :as yaml]
            [clojure.java.io :as io]
            [clojure.java.shell :as sh]
            [clojure.string :as s]
            [lein-environ.plugin]))

(defn docker-compose-file
  [project]
  (io/file (:root project) "docker-compose.yml"))

(defn extract-container-port
  [port-config]
  (-> (str port-config)
      (s/split #":")
      (last)))

(defn get-exposed-ports
  [services]
  (for [[service config] services
        port-config      (:ports config)]
    [(name service) (extract-container-port port-config)]))

(defn config-key
  [[service container-port]]
  (keyword (str "docker-" service "-port-" container-port)))

(defn discover-port-mapping
  [[service container-port]]
  (let [output (sh/sh "docker-compose" "port" service container-port)]
    (when (zero? (:exit output))
      (s/replace (:out output) #"^.*:(.*)\n$" "$1"))))

(defn discover-docker-ports
  [project]
  (if-not (.exists (docker-compose-file project))
    (println "WARNING: Could not find docker-compose.yml")
    (->> (slurp (docker-compose-file project))
         (yaml/parse-string)
         (get-exposed-ports)
         (map (juxt config-key discover-port-mapping))
         (into {}))))

(defn merge-docker-env-vars
  [func project]
  (merge (func project)
         (discover-docker-ports project)))

(defn hooks []
  (add-hook #'lein-environ.plugin/read-env #'merge-docker-env-vars))
