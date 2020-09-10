(ns proxy-protect.components.rules
  (:require [clojure.tools.logging :refer [info]]
            [com.stuartsierra.component :as component]
            [cheshire.core :as json]))

(defn http-method-allowed?
  [allowed-methods request-method]
  (let [string-request-method (name request-method)]
    (some #(or (= % "any")
               (= % string-request-method))
          allowed-methods)))

(defmulti create-match-fn :type)
(defmethod create-match-fn "absolute"
  [rule]
  (let [allowed-methods (:allowedMethods rule)]
    (assoc rule :match-fn (fn [request]
                            (let [{:keys [request-method uri]} request]
                              (and (http-method-allowed? allowed-methods
                                                         request-method)
                                   (= uri (:source rule))))))))
(defmethod create-match-fn "regex"
  [rule]
  (let [pattern (re-pattern (:source rule))
        allowed-methods (:allowedMethods rule)]
    (assoc rule :match-fn (fn [request]
                            (let [{:keys [request-method uri]} request]
                              (and (http-method-allowed? allowed-methods
                                                         request-method)
                                   (re-matches pattern uri)))))))
(defmethod create-match-fn "redirect"
  [rule]
  (assoc rule :match-fn (fn [request]
                          (let [{:keys [request-method uri]} request]
                            (and (= request-method :get)
                                 (= uri (:source rule)))))))

(defn- rules->edn
  [rules]
  (json/decode rules true))

(defrecord Rules [rules]
  component/Lifecycle
  (start [component]
    (info "Starting the Rules Component...")
    (assoc component :rules (->> rules
                                 slurp
                                 rules->edn
                                 (mapv create-match-fn))))
  (stop [component]
    (info "Stopping the Rules Component...")
    (assoc component :rules nil)))
