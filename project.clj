(defproject sleepytime "0.1.0-SNAPSHOT"
  :description "Commandline sleep time recorder"
  :url "http://github.com/CodeFarmer/sleepytime"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/java.jdbc "0.7.8"]
                 [clojure.java-time "0.3.2"]
                 [org.xerial/sqlite-jdbc "3.25.2"]
                 [environ "1.1.0"]]
  
  :main ^:skip-aot sleepytime.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
