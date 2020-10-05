(ns lollipop.components.webserver
  (:require [clojure.tools.logging :refer [info error]]
            [com.stuartsierra.component :as component]
            [org.httpkit.server :as server]))


(defrecord WebServer [handler
                      rules
                      webserver]
  component/Lifecycle
  (start [component]
    (info "Starting Webserver Component...")
    (let [rules (:rules rules)]
      (assoc component :webserver (server/run-server (handler rules)
                                                     {:host "0.0.0.0"
                                                      :port 3000}))))
  (stop [component]
    (info "Stopping Webserver Component...")
    (try
      (when webserver
        (webserver))
      (catch Exception e
        (error e (str "Error stopping Webserver."))))
    (info "Stopped Webserver Component.")
    (assoc component :handler nil
                     :rules nil
                     :webserver nil)))
