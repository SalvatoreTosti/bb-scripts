#! /usr/bin/env bb
(require '[babashka.pods :as pods])

(pods/load-pod 'org.babashka/postgresql "0.0.7")
(require '[pod.babashka.postgresql :as pg])

(defn parse-db-spec [database_url]
  (let [matcher (re-matcher #"postgres://(\w*):(\w*)@([\w\-\.]*):(\d*)/(\w*)" database_url)
        [_ user password host port dbname] (re-find matcher)]
    {:dbtype "postgresql"
     :host host
     :dbname dbname
     :user user
     :password password
     :port port}))

(defn env->db-spec []
  (if-let [database_url (System/getenv "DATABASE_URL")]
    (parse-db-spec database_url)
    (do
      (println "DATABASE_URL variable is not set.")
      (System/exit 1))))

(when-let [db (env->db-spec)]
  (pg/execute! db ["SELECT * FROM public.\"user\" ORDER BY id ASC"]))
