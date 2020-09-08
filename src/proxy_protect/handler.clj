(ns proxy-protect.handler
  (:require [compojure.core :refer [routes wrap-routes ANY]]
            [clojure.tools.logging :refer [warn]]
            [ring.util.response :refer [response status]]
            [proxy-protect.components.rules :refer [proxy-request]]))

(defonce test-unprotected-routes
  (ANY "/insecure" [] "<h1>Insecure Example</h1>"))

(defonce test-protected-routes
  (ANY "*" []))

(defn- wrap-proxy
  [_handler-fn rules]
  (fn [request]
    (loop [[{:keys [rule-fn destination] :as rule} & rules'] rules]
      (if rule
        (if (rule-fn request)
          (proxy-request request destination)
          (recur rules'))
        (do (warn "No proxy for URI" (:uri request))
            (-> "No Route to Proxy"
                response
                (status 404)))))))

(defn handler
  [rules]
  (routes test-unprotected-routes
          (wrap-routes
            test-protected-routes
            wrap-proxy rules)))
