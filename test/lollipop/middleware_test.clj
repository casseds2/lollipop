(ns lollipop.middleware-test
  (:require [clojure.test :refer :all]
            [lollipop.middleware :as middleware]))

(deftest request-method->string-test
  (is (= "GET" (middleware/request-method->string :get)))
  (is (= "PUT" (middleware/request-method->string :put)))
  (is (= "POST" (middleware/request-method->string :post)))
  (is (= "DELETE" (middleware/request-method->string :delete)))
  (is (= "PATCH" (middleware/request-method->string :patch)))
  (is (= "GET" (middleware/request-method->string :unknown))))
