(ns lein-docker-compose.plugin
  (:use [robert.hooke :only (add-hook)])
  (:require [yaml.core :as yaml]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.java.shell :as sh]
            [clojure.string :as s]
            [lein-environ.plugin]))

(defn- docker-env-file
  "Searches for a .lein-docker-env in the current and parent directories,
   to a maximum of three level of parents. Stops if it has reached the git
   root without finding one."
  ([project]
   (docker-env-file project "."))
  ([project dir]
   (let [f        (io/file (:root project) dir ".lein-docker-env")
         git-root (io/file (:root project) dir ".git")]
     (cond (.exists f)         f
           (.exists git-root)  nil
           (= "../../../" dir) nil
           :else               (recur project (str "../" dir))))))

(defn discover-docker-ports
  [project]
  (some-> (docker-env-file project)
          (slurp)
          (edn/read-string)))

(defn merge-docker-env-vars
  [func project]
  (merge (func project)
         (discover-docker-ports project)))

(defn hooks []
  (add-hook #'lein-environ.plugin/read-env #'merge-docker-env-vars))
