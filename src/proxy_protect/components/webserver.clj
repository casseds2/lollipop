(ns proxy-protect.components.webserver
  (:require [clojure.tools.logging :refer [info error]]
            [com.stuartsierra.component :as component]
            [ring.adapter.jetty :refer [run-jetty]])
  (:import org.eclipse.jetty.server.Server))


(defrecord WebServer [handler
                      rules
                      webserver]
  component/Lifecycle
  (start [component]
    (info "Starting Webserver Component...")
    (let [rules (:rules rules)]
      (assoc component :webserver
                       (run-jetty (handler rules)
                                  {:host "0.0.0.0"
                                   :port 3000
                                   :join? false}))))
  (stop [component]
    (info "Stopping Webserver Component...")
    (try
      (if-let [webserver (:webserver component)]
        (.stop ^Server webserver)
        (.destroy ^Server webserver))
      (catch Exception e
        (error e (str "Error stopping Webserver"))
        (assoc component :handler nil
                         :webserver nil)))
    (info "Stopped Webserver Component.")
    (assoc component :handler nil
                     :rules nil
                     :webserver nil)))
