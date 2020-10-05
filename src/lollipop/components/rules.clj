(ns lollipop.components.rules
  (:require [clojure.tools.logging :refer [info warnf debug]]
            [com.stuartsierra.component :as component]
            [clojure.spec.alpha :as spec]
            [cheshire.core :as json]))

;; TODO - Different validation for every type of rule

(spec/def ::type #{"regex" "exact" "redirect"})
(spec/def ::allowedMethods #(every? #{"get" "put" "post" "delete" "patch" "any"} %))
(spec/def ::source #(and (string? %) (clojure.string/starts-with? % "/")))
(spec/def ::destination string?)
(spec/def ::rule (spec/keys :req-un [::type ::source ::destination]
                            :opt-un [::allowedMethods]))

(defn valid-rule?
  [rule]
  (let [valid? (spec/valid? ::rule rule)
        {:keys [type source destination]} rule]
    (when (false? valid?)
      (warnf "Invalid %s Rule %s -> %s"
              (clojure.string/capitalize type)
              source
              destination)
      (debug (spec/explain-str ::rule rule)))
    valid?))

(defn http-method-allowed?
  [allowed-methods request-method]
  (let [string-request-method (name request-method)]
    (some #(or (= % "any")
               (= % string-request-method))
          allowed-methods)))

(defmulti create-rule-fns :type)
(defmethod create-rule-fns "exact"
  [rule]
  (let [allowed-methods (:allowedMethods rule)]
    (assoc rule :match-fn (fn [request]
                            (let [{:keys [request-method uri]} request]
                              (and (http-method-allowed? allowed-methods
                                                         request-method)
                                   (= uri (:source rule)))))
                :destination-fn (constantly (:destination rule)))))

(defmethod create-rule-fns "regex"
  [rule]
  (let [pattern (re-pattern (:source rule))
        allowed-methods (:allowedMethods rule)]
    (assoc rule :match-fn (fn [request]
                            (let [{:keys [request-method uri]} request]
                              (and (http-method-allowed? allowed-methods
                                                         request-method)
                                   (re-matches pattern uri))))
                :destination-fn (constantly (:destination rule)))))

(defmethod create-rule-fns "redirect"
  [rule]
  (assoc rule :match-fn (fn [request]
                          (let [{:keys [request-method uri]} request]
                            (and (= request-method :get)
                                 (= uri (:source rule)))))
              :destination-fn (constantly (:destination rule))))

;; TODO - Create a regex for matching the partial source
;; TODO - Create the destination from the partial route
;(defmethod create-rule-fns "partial"
;  [rule]
;  (let [allowed-methods (:allowedMethods rule)]
;    (assoc rule :match-fn (fn [request]
;                            (let [{:keys [request-method uri]} request]
;                              (and (http-method-allowed? allowed-methods
;                                                         request-method))))
;                :destination-fn (constantly (:destination rule))))

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
                                 (filterv valid-rule?)
                                 (mapv create-rule-fns))))
  (stop [component]
    (info "Stopping the Rules Component...")
    (assoc component :rules nil)))
