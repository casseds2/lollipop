(ns proxy-protect.components.rules-test
  (:require [clojure.test :refer :all]
            [proxy-protect.components.rules :as rules]))

(deftest http-method-allowed?-test
  (is (true? (rules/http-method-allowed? ["any"] :post)))
  (is (true? (rules/http-method-allowed? ["get" "post"] :get)))
  (is (nil? (rules/http-method-allowed? ["get"] :post)))
  (is (nil? (rules/http-method-allowed? [] :post))))
