(defproject proxy-protect "0.1.0-SNAPSHOT"
  :description "Proxy Protect"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/tools.cli "1.0.194"]
                 [org.clojure/tools.logging "1.1.0"]
                 [org.apache.logging.log4j/log4j-slf4j-impl "2.13.3"]
                 [com.stuartsierra/component "1.0.0"]
                 [cheshire "5.10.0"]
                 [http-kit "2.4.0-alpha5"]
                 [clj-http "3.10.2"]
                 [compojure "1.6.2"]]
  :main proxy-protect.core)
