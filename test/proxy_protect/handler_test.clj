(ns proxy-protect.handler-test
  (:require [clojure.test :refer :all]
            [proxy-protect.handler :as handler]))

(deftest request-method->string-test
  (is (= "GET" (handler/request-method->string :get)))
  (is (= "PUT" (handler/request-method->string :put)))
  (is (= "POST" (handler/request-method->string :post)))
  (is (= "DELETE" (handler/request-method->string :delete)))
  (is (= "PATCH" (handler/request-method->string :patch)))
  (is (= "GET" (handler/request-method->string :unknown))))
