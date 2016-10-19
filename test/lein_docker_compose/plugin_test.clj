(ns lein-docker-compose.plugin-test
  (:require [clojure.test :refer :all]
            [yaml.core :as yaml]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [lein-docker-compose.plugin :as l]))

(deftest extracts-ports-from-config
  (is (= [["foo" "8000"]
          ["foo" "4567"]
          ["foo" "5555"]
          ["bar" "9000"]]
         (-> (slurp (io/resource "example.yml"))
             (yaml/parse-string)
             (l/get-exposed-ports)))))

(deftest config-key-test
  (is (= :docker-foo-port-4567
         (l/config-key ["foo" "4567"]))))
