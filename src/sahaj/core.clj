(ns sahaj.core
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.spec.alpha :as spec])
  (:gen-class))

(spec/def ::stockid string?)
(spec/def ::company string?)
(spec/def ::side #{"Buy" "Sell"})
(spec/def ::quantity number?)
(spec/def ::remaining number?)
(spec/def ::status #{"OPEN" "CLOSED"})

(spec/def ::order (spec/keys :req-un [::stockid ::company ::side ::quantity
                                      ::remaining ::status]))

(spec/def ::unprocessed-order (spec/keys :req-un [::stockid ::company ::side ::quantity]))

(defn cumulative-sum
  "Cumulative sum of the quantity of orders"
  [orders]
  (reductions +' (map :quantity orders)))

(spec/fdef cumulative-sum
           :args (spec/cat :orders (spec/coll-of ::order))
           :ret (spec/coll-of number?))

(defn update-remaining
  "Update the cumulative quantity sequence as remaining orders"
  [orders cumulative-sum]
  (map #(assoc %1 :remaining %2) orders cumulative-sum))

(spec/fdef update-remaining
           :args (spec/cat :orders (spec/coll-of ::unprocessed-order)
                           :cumulative-sum (spec/coll-of number?))
           :ret (spec/coll-of ::unprocessed-order))

(defn close-orders
  "Update orders as CLOSED with remaining orders as 0"
  [orders]
  (map #(assoc %1 :status "CLOSED" :remaining 0) orders))

(spec/fdef close-orders
           :args (spec/cat :orders (spec/coll-of ::order))
           :ret (spec/coll-of ::order))

(defn open-orders
  "Update orders as OPEN or CLOSED based on remaining orders"
  [orders open-total]
  (for [order orders
        :let [remaining (- (:remaining order) open-total)]]
    (if (pos? remaining)
      (assoc order :remaining remaining :status "OPEN")
      (assoc order :remaining 0 :status "CLOSED"))))

(spec/fdef open-orders
           :args (spec/cat :orders (spec/coll-of ::order) :open-total nat-int?)
           :ret (spec/coll-of ::order))

(defn process-orders
  "Process orders.
  If the buy quantity is greater than sell quantity then close all the sell orders and update buy orders status
  If the sell quantity is greater than buy quantity then close all the buy orders and update sell orders status
  If they both buy and sell quantity are equal then close all orders
  "
  [orders]
  (let [{sell "Sell", buy "Buy"} (group-by :side orders)
        sell-cumulative-sum    (cumulative-sum sell)
        buy-cumulative-sum     (cumulative-sum buy)
        buy-total              (last buy-cumulative-sum)
        sell-total             (last sell-cumulative-sum)
        buy-orders             (update-remaining (or buy []) buy-cumulative-sum)
        sell-orders            (update-remaining (or sell []) sell-cumulative-sum)]
    (cond
      (> buy-total sell-total)
      (concat (open-orders buy-orders sell-total) (close-orders sell-orders))
      (< buy-total sell-total)
      (concat (open-orders sell-orders buy-total) (close-orders buy-orders))
      :else
      (close-orders orders))))

(spec/fdef process-orders
           :args (spec/cat :orders (spec/coll-of ::unprocessed-order :min-count 1))
           :ret (spec/coll-of ::order))

(defn transform-map
  "Coercions for the order map"
  [orders]
  (map #(update %1 :quantity (fn [quantity] (Integer/parseInt quantity))) orders))

(spec/fdef process-orders
           :args (spec/cat :orders (spec/coll-of ::order))
           :ret (spec/cat :orders (spec/coll-of ::order)))

(defn csv-data->orders
  "Parse CSV data as a map of orders"
  [csv-data]
  (map zipmap
       (->> (first csv-data)
            (map (comp keyword clojure.string/lower-case))
            repeat)
       (rest csv-data)))

(spec/fdef maps->csv-data
           :args (spec/cat :csv-data seq?)
           :ret (spec/coll-of ::order))

(defn maps->csv-data
  "Format map appropriately for CSV"
  [orders]
  (let [headers (->> (first orders)
                     keys
                     (mapv (comp clojure.string/capitalize name)))]
    (apply vector headers (map vals orders))))

(spec/fdef maps->csv-data
           :args (spec/cat :orders (spec/coll-of ::order))
           :ret vector?)

(defn write-to-file
  "Write the orders to the given output file"
  [orders output]
  (with-open [writer (io/writer output)]
    (->> orders
         maps->csv-data
         (csv/write-csv writer))))

(spec/fdef write-to-file
           :args (spec/cat :orders (spec/coll-of ::order) :filename string?))

(defn process-file
  "Process the given CSV file of orders"
  [input]
  (with-open [reader (io/reader input)]
    (->> (csv/read-csv reader)
         csv-data->orders
         transform-map
         (group-by :company)
         vals
         (mapcat process-orders)
         (sort-by (comp #(Integer/parseInt %1) :stockid)))))

(spec/fdef process-file
           :args (spec/cat :input string?))

(defn -main [& args]
  (let [input (first args)
        output (second args)]
    (-> input
        process-file
        (write-to-file output))))
