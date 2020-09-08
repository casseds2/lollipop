(ns proxy-protect.components.rules
  (:require [clojure.tools.logging :refer [info]]
            [com.stuartsierra.component :as component]
            [jsonista.core :as json]
            [org.httpkit.client :as client]))

(defmulti request-type :request-method)
(defmethod request-type :get [_] client/get)
(defmethod request-type :post [_] client/post)
(defmethod request-type :put [_] client/put)
(defmethod request-type :delete [_] client/delete)
(defmethod request-type :patch [_] client/patch)

(defn proxy-request
  [request destination]
  (let [request-fn (request-type request)
        response @(request-fn destination {:throw-exceptions false})]
    (assoc response :headers nil)))

(defn- rules->edn
  [rules]
  (let [object-mapper (json/object-mapper {:decode-key-fn true})]
    (json/read-value rules object-mapper)))

(defn- rule->rule-fn
  [rule]
  (assoc rule :rule-fn (fn [request]
                         (let [uri (:uri request)]
                           (= uri (:source rule))))))

(defrecord Rules [rules]
  component/Lifecycle
  (start [component]
    (info "Starting the Rules Component...")
    (assoc component :rules (->> rules
                                 slurp
                                 rules->edn
                                 (mapv rule->rule-fn))))
  (stop [component]
    (info "Stopping the Rules Component...")
    (assoc component :rules nil)))
