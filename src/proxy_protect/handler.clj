(ns proxy-protect.handler
  (:require [compojure.core :refer [routes GET]]))

(defn- app-routes
  [_rules]
  (routes (GET "local" [] {:hello "world"})))

(defn handler
  [rules]
  (app-routes rules))
