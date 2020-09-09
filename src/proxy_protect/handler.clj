(ns proxy-protect.handler
  (:require [compojure.core :refer [routes wrap-routes ANY]]
            [clojure.tools.logging :refer [warnf]]
            [ring.util.response :refer [response status]]
            [proxy-protect.http :refer [proxy-request]]))

(defonce test-unprotected-routes
  (routes (ANY "/abs-insecure" [] "<h1>Absolute Insecure Example</h1>")
          (ANY "/reg-insecure" [] "<h1>Regex Insecure Example</h1>")))

(defonce test-protected-routes
  (ANY "*" []))

(defn request-method->string
  [request-method]
  (case request-method
    :get "GET"
    :put "PUT"
    :post "POST"
    :delete "DELETE"
    :patch "PATCH"
    "GET"))

(defn- respond-not-found
  [request]
  (warnf "No proxy for method %s on URI %s"
         (request-method->string (:request-method request))
         (:uri request))
  (-> "No Route to Proxy"
      response
      (status 404)))

(defn- wrap-proxy
  [_handler-fn rules]
  (fn [request]
    (loop [[{:keys [match-fn destination] :as rule} & rules'] rules]
      (if rule
        (if (match-fn request)
          (proxy-request request destination)
          (recur rules'))
        (respond-not-found request)))))

(defn handler
  [rules]
  (routes test-unprotected-routes
          (wrap-routes
            test-protected-routes
            wrap-proxy rules)))
