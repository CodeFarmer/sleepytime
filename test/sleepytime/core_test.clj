(ns sleepytime.core-test
  (:require [clojure.test :refer :all]
            [sleepytime.core :refer :all]
            [java-time :refer [local-date-time]]))


(deftest test-date-string
  (testing "Happy path date string formatting"
    (is (= "2019-01-21 14:45:21" (date-string (local-date-time 2019 01 21 14 45 21))))))

(deftest test-parse-clocktime
  (testing "Happy path, valid time string"
    (is (= java.time.Duration (class (parse-clocktime "12:34"))) "returns a Duration"))
  (testing "Invalid time string"
    (is (nil? (parse-clocktime "something")) "returns nil")))
