(defproject proxy-protect "0.1.0-SNAPSHOT"
  :description "Proxy Protect"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/tools.cli "1.0.194"]
                 [org.clojure/tools.logging "1.1.0"]
                 [com.stuartsierra/component "1.0.0"]
                 [metosin/jsonista "0.2.7"]
                 [http-kit "2.4.0"]
                 [compojure "1.6.2"]]
  :main proxy-protect.core)
