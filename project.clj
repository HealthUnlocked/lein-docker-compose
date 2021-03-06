(defproject healthunlocked/lein-docker-compose "0.2.1"
  :description "Leiningen plugin that provides environ with docker-compose port mappings"
  :url "https://github.com/healthunlocked/lein-docker-compose"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.flatland/ordered "1.5.7"]
                 [io.forward/yaml "1.0.9" :exclusions [org.flatland/ordered]]
                 [lein-environ "1.1.0"]]
  :profiles {:dev {:resource-paths ["test/resources"]
                   :dependencies [[lein-ancient "0.6.15"]]}}
  :deploy-repositories [["clojars" {:username :env/clojars_username
                                    :password :env/clojars_password
                                    :sign-releases false
                                    :url "https://clojars.org/repo/"}]]
  :eval-in-leiningen true)
