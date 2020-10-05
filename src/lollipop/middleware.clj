(ns lollipop.middleware
  (:require [clojure.tools.logging :refer [warnf]]
            [ring.util.response :refer [response status]]
            [lollipop.http :as http]))

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

;; TODO - add metrics on requests

(defn wrap-proxy
  [_handler-fn rules]
  (fn [request]
    (loop [[{:keys [match-fn] :as rule} & rules'] rules]
      (if rule
        (if (match-fn request)
          (http/proxy-request request rule)
          (recur rules'))
        (respond-not-found request)))))
