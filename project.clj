(defproject sahaj "0.1.0-SNAPSHOT"
  :description "A simple CSV tool to execute stock orders"
  :url "http://github.com/tirkarthi/sahaj-csv"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/test.check "0.9.0"]
                 [org.clojure/data.csv "0.1.4"]]
  :main sahaj.core
  :target-path "doc/"
  :plugins [[lein-codox "0.10.3"]]
  :jvm-opts ^replace [])
