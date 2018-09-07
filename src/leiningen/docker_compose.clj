(ns leiningen.docker-compose
  (:use [robert.hooke :only (add-hook)])
  (:require [yaml.core :as yaml]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.java.shell :as sh]
            [clojure.string :as s]))

(defn- docker-compose-file
  "Searches for a docker-compose.yml in the current and parent directories,
   to a maximum of three level of parents. Stops if it has reached the git
   root without finding one."
  ([project]
   (docker-compose-file project "."))
  ([project dir]
   (let [dc-yml   (io/file (:root project) dir "docker-compose.yml")
         git-root (io/file (:root project) dir ".git")]
     (cond (.exists dc-yml)    dc-yml
           (.exists git-root)  nil
           (= "../../../" dir) nil
           :else               (recur project (str "../" dir))))))

(defn extract-container-port
  [port-config]
  (-> (str port-config)
      (s/split #":")
      (last)))

(defn get-exposed-ports
  "docker-compose.yml structure has changed, and services are now nested
   under a :services key. we check both places to support both old and new"
  [services]
  (for [[service config] (or (:services services) services)
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

(defn docker-compose
  [project & [dir]]
  (if-let [dc-yml (docker-compose-file project (or dir "."))]
    (->> (slurp dc-yml)
         (yaml/parse-string)
         (get-exposed-ports)
         (map (juxt config-key discover-port-mapping))
         (into {})
         (pr-str)
         (spit (io/file (:root project) ".lein-docker-env")))
    (println "Couldn't find docker-compose.yml")))
