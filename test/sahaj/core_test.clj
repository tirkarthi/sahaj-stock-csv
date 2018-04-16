(ns sahaj.core-test
  (:require [clojure.test :refer :all]
            [sahaj.core :refer :all]
            [clojure.spec.test.alpha :as stest]))

(deftest test-simple
  (testing "Test sample"
    (is (= (process-file "sample.csv")
           '({:stockid "1", :side "Buy", :company "ABC", :quantity 10, :remaining 0, :status "CLOSED"}
             {:stockid "2", :side "Sell", :company "XYZ", :quantity 15, :remaining 0, :status "CLOSED"}
             {:stockid "3", :side "Sell", :company "ABC", :quantity 13, :remaining 3, :status "OPEN"}
             {:stockid "4", :side "Buy", :company "XYZ", :quantity 10, :remaining 0, :status "CLOSED"}
             {:stockid "5", :side "Buy", :company "XYZ", :quantity 8, :remaining 3, :status "OPEN"})
           ))))

(deftest test-only-one-type-side
  (testing "Buy only"
    (let [orders   [{:stockid "1", :side "Buy", :company "ABC", :quantity 10}
                    {:stockid "2", :side "Buy", :company "ABC", :quantity 15}]
          expected '({:stockid "1", :side "Buy", :company "ABC", :quantity 10, :remaining 10, :status "OPEN"}
                     {:stockid "2", :side "Buy", :company "ABC", :quantity 15, :remaining 25, :status "OPEN"})]
      (is (= (process-orders orders) expected))))
  (testing "Sell only"
    (let [orders   [{:stockid "1", :side "Sell", :company "ABC", :quantity 11}
                    {:stockid "2", :side "Sell", :company "ABC", :quantity 15}]
          expected '({:stockid "1", :side "Sell", :company "ABC", :quantity 11, :remaining 11, :status "OPEN"}
                     {:stockid "2", :side "Sell", :company "ABC", :quantity 15, :remaining 26, :status "OPEN"})]
      (is (= (process-orders orders) expected)))))

(deftest test-equal-buy-sell
  (testing "Test equal amount of buy and sell"
    (let [orders   [{:stockid "1", :side "Buy", :company "ABC", :quantity 10}
                    {:stockid "2", :side "Sell", :company "ABC", :quantity 10}]
          expected '({:stockid "1", :side "Buy", :company "ABC", :quantity 10, :remaining 0, :status "CLOSED"}
                     {:stockid "2", :side "Sell", :company "ABC", :quantity 10, :remaining 0, :status "CLOSED"})]
      (is (= (process-orders orders) expected)))))

(deftest test-cumulative-sum
  (testing "Test cumulative sum"
    (let [orders   [{:stockid "1", :side "Buy", :company "ABC", :quantity 10}
                    {:stockid "2", :side "Buy", :company "ABC", :quantity 10}]
          expected '(10 20)]
      (is (= (cumulative-sum orders) expected)))))

(deftest test-close-orders
  (testing "Test close orders"
    (let [orders   [{:stockid "1", :side "Buy", :company "ABC", :quantity 10}
                    {:stockid "2", :side "Buy", :company "ABC", :quantity 10}]
          expected '({:stockid "1", :side "Buy", :company "ABC", :quantity 10, :remaining 0, :status "CLOSED"}
                     {:stockid "2", :side "Buy", :company "ABC", :quantity 10, :remaining 0, :status "CLOSED"})]
      (is (= (close-orders orders) expected)))))

;; spec tests

(stest/check `process-orders)

(stest/check `update-remaining)

(stest/check `cumulative-sum)

(stest/check `close-orders)

(stest/check `open-orders)
