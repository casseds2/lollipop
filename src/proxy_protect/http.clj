(ns proxy-protect.http
  (:require [clj-http.client :as http]
            [clojure.tools.logging :refer [info]]))

(defmulti request-type :request-method)
(defmethod request-type :get [_] http/get)
(defmethod request-type :post [_] http/post)
(defmethod request-type :put [_] http/put)
(defmethod request-type :delete [_] http/delete)
(defmethod request-type :patch [_] http/patch)

(defn proxy-request
  [request destination]
  (let [request-fn (request-type request)]
    (request-fn destination
                {:headers (:headers request)
                 :body (:body request)})))
