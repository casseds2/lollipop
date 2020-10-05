(ns lollipop.core
  (:require [clojure.tools.cli :as cli]
            [com.stuartsierra.component :as component]
            [clojure.tools.logging :refer [info error]]
            [lollipop.handler :refer [handler]]
            [lollipop.components.rules :as rules]
            [lollipop.components.webserver :as webserver]))

(def system nil)

(defn- create-system
  [rules]
  (component/system-map
    :rules (rules/->Rules rules)
    :webserver (component/using (webserver/map->WebServer {:handler handler})
                                [:rules])))

(defn- init
  [rules]
  (alter-var-root #'system (fn [_] (create-system rules))))

(defn- stop
  []
  (info "Stopping Lollipop...")
  (alter-var-root #'system
                  (fn [sys]
                    (if sys
                      (component/stop sys)
                      (error "System not initialised. Stop failed.")))))

(defn- start
  []
  (info "Starting Lollipop...")
  (if system
    (try
      (alter-var-root #'system component/start)
      (catch Exception e
        (error e "Could not start system")
        (stop)))
    (error "System not initialised.")))

(defonce cli-opts
  [["-r" "--rules RULES" "Path to rules.json"
    :default "resources/rules.json"]])

(defn -main
  [& args]
  (try
      (let [{rules :rules} (:options (cli/parse-opts args cli-opts))]
        (init rules)
        (start))
      (catch Exception e
        (error "Error starting Lollipop." e))))
