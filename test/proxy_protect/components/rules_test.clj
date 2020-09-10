(ns proxy-protect.components.rules-test
  (:require [clojure.test :refer :all]
            [clojure.tools.logging.test :refer [with-log]]
            [proxy-protect.components.rules :as rules]))

(deftest http-method-allowed?-test
  (is (true? (rules/http-method-allowed? ["any"] :post)))
  (is (true? (rules/http-method-allowed? ["get" "post"] :get)))
  (is (nil? (rules/http-method-allowed? ["get"] :post)))
  (is (nil? (rules/http-method-allowed? [] :post))))

(deftest valid-rule?-test
  (with-log
    (is (false? (rules/valid-rule? {:type "absolute"})))
    (is (false? (rules/valid-rule? {:type "absolute"
                                    :allowedMethods ["get"]
                                    :source "some-source"
                                    :destination "http://some-destination/endpoint"})))
    (is (true? (rules/valid-rule? {:type "absolute"
                                   :allowedMethods ["get"]
                                   :source "/some-source"
                                   :destination "http://some-destination/endpoint"})))))
