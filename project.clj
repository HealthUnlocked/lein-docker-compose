(defproject healthunlocked/lein-docker-compose "0.1.1-SNAPSHOT"
  :description "Leiningen plugin that provides environ with docker-compose port mappings"
  :url "https://github.com/healthunlocked/lein-docker-compose"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[io.forward/yaml "1.0.5"]
                 [lein-environ "1.1.0"]]
  :profiles {:dev {:resource-paths ["test/resources"]
                   :dependencies [[lein-ancient "0.6.10"]]}}
  :deploy-repositories [["clojars" {:username :env/clojars_username
                                    :password :env/clojars_password
                                    :url "https://clojars.org/repo/"}]]
  :eval-in-leiningen true)
