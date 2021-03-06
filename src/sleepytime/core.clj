(ns sleepytime.core
  (:require [clojure.java.jdbc :as jdbc]
            [environ.core :refer [env]]
            [java-time :as jt])
  (:gen-class))


(def db
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     (str (or (env :sleepytime-db-dir)
                         (str (env :home) "/.sleepytime")) "/sleepytime.db")})

(defn create-db
  "create db and table"
  []
  (try (jdbc/db-do-commands
        db
        (jdbc/create-table-ddl :sleeps
                               [[:timestamp :datetime :default :current_timestamp]
                                [:start     :datetime :not :null]
                                [:end       :datetime :not :null]
                                [:good      :boolean :default :true]
                                [:comment   :text]]
                               {:conditional? true}))
       (catch Exception e
         (println (.getMessage e)))))


(defn all-sleeps
  "retrieve lazy seq of all sleeps in the database, most recent first"
  []
  (jdbc/query db ["SELECT * FROM sleeps ORDER BY timestamp DESC"]))


(defn date-string
  "Convert a Java date to an ISO string"
  [date]
  (jt/format "yyyy-MM-dd HH:mm:ss" date))


(defn parse-clocktime
  "Convert a time string (eg., \"06:30\") to a Duration"
  [timestring]
  (let [[_ hrs mins] (re-find (re-matcher #"(\d\d):(\d\d)" timestring))]
    (when (and hrs mins)
      (jt/plus (jt/hours (Integer/parseInt hrs)) (jt/minutes (Integer/parseInt mins))))))


(defn today-midnight []
  (jt/truncate-to (jt/local-date-time) :days))

(defn yesterday-midnight []
  (jt/minus (today-midnight) (jt/days 1)))


(defn duration-to-datetime
  "Align a Duration (basically a clock time) to the appropriate day - if before midday, to today, if after midday, to yesterday - and return the appropriate datetime"
  [duration]
  (if (= 1 (.compareTo duration (jt/hours 12)))
    (jt/plus (yesterday-midnight) duration)
    (jt/plus (today-midnight) duration)))


(defn add-sleep

  ([start end good? comment]
   (let [start-clocktime (parse-clocktime start)
         end-clocktime   (parse-clocktime end)]
     (if (and start-clocktime end-clocktime)
       (jdbc/insert! db :sleeps {:start (date-string (duration-to-datetime start-clocktime))
                                 :end   (date-string (duration-to-datetime end-clocktime))
                                 :good  good?
                                 :comment comment}))))

  ([start end]
   (add-sleep start end true nil)))

(comment
  (jt/plus (jt/truncate-to (jt/local-date-time) :days) (jt/hours (Integer/parseInt "06"))))



(defn -main
  [& args]
  (let [[command & cargs] args]
    (case command
      "sleep" (apply add-sleep cargs)
      "show" (doseq [s (all-sleeps)] (println s)))))
