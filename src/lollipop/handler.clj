(ns lollipop.handler
  (:require [compojure.core :refer [routes wrap-routes context GET POST ANY]]
            [lollipop.middleware :refer [wrap-proxy]]))

(defonce test-unprotected-routes
  (routes (context "/insecure" []
            (GET "/" [] "<h1>GET Insecure Example</h1>")
            (POST "/" [] "<h1>POST Insecure Example</h1>")
            (GET "/partial" [] "<h1>GET Insecure Partial Example</h1>"))))

(defonce test-protected-routes
  (ANY "*" []))

(defn handler
  [rules]
  (routes test-unprotected-routes
          (wrap-routes
            test-protected-routes
            wrap-proxy rules)))
