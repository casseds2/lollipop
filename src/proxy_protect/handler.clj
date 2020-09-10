(ns proxy-protect.handler
  (:require [compojure.core :refer [routes wrap-routes GET POST ANY]]
            [clojure.tools.logging :refer [warnf]]
            [ring.util.response :refer [response status]]
            [proxy-protect.http :as http]))

(defonce test-unprotected-routes
  (routes (GET "/insecure" [] "<h1>GET Insecure Example</h1>")
          (POST "/insecure" [] "<h1>POST Insecure Example</h1>")))

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
    (loop [[{:keys [match-fn] :as rule} & rules'] rules]
      (if rule
        (if (match-fn request)
          (http/proxy-request request rule)
          (recur rules'))
        (respond-not-found request)))))

(defn handler
  [rules]
  (routes test-unprotected-routes
          (wrap-routes
            test-protected-routes
            wrap-proxy rules)))
