(ns proxy-protect.components.rules
  (:require [clojure.tools.logging :refer [info error]]
            [com.stuartsierra.component :as component]
            [jsonista.core :as json]))

(defrecord Rules [rules]
  component/Lifecycle
  (start [component]
    (info "Starting the Rules Component...")
    (assoc component :rules (json/read-value (slurp rules))))
  (stop [component]
    (info "Stopping the Rules Component...")
    (assoc component :rules nil)))