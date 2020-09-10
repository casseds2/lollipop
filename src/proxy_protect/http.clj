(ns proxy-protect.http
  (:require [clj-http.client :as http]
            [clojure.tools.logging :refer [error debug]]
            [ring.util.response :refer [response status redirect]]
            [clojure.core.async :as async]))

(defmulti request-type :request-method)
(defmethod request-type :get [_] http/get)
(defmethod request-type :post [_] http/post)
(defmethod request-type :put [_] http/put)
(defmethod request-type :delete [_] http/delete)
(defmethod request-type :patch [_] http/patch)

(defn- handle-response
  [response-channel response]
  (debug "Handling proxied response" response)
  (async/put! response-channel response))

(defn- handle-exception
  [response-channel exception]
  (error "Error executing proxy request" exception)
  (async/put! response-channel (-> "Internal Server Error"
                                   response
                                   (status 500))))

(defn- async-request
  [request destination]
  (let [request-fn (request-type request)
        response-channel (async/chan)]
    (try
      (request-fn destination
                  {:async? true
                   :headers (:headers request)
                   :body (:body request)}
                  (partial handle-response response-channel)
                  (partial handle-exception response-channel))
      (catch Exception e (debug e)))
    response-channel))

(defn proxy-request
  [request rule]
  (let [{:keys [destination type]} rule]
    (case type
      "redirect" (redirect destination)
      (async/<!!
        (async/go (-> (async-request request destination)
                      async/<!))))))
