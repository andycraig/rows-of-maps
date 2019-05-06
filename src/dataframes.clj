(ns dataframes
  (:use [clojure.repl])
  (:require [clojure.pprint :refer [pprint print-table]]))

(defn mutate-out-one
  [row new-col-name fn-of-nested]
  (assoc row new-col-name (fn-of-nested (:nested row))))

(defn mutate-out
  [rows new-col-name fn-of-nested]
  (map (fn [x] (mutate-out-one x new-col-name fn-of-nested)) rows))

(defn mutate-in-nested
  [nested new-col-name f]
  (map #(assoc % new-col-name (f %))
       nested)))

(defn mutate-in-one
  [row new-col-name f]
  (update row ; the map
          :nested ; the key
          (fn [x] (mutate-in-nested x new-col-name f))))

(defn mutate-in
  [rows new-col-name fn-of-nested]
  (map (fn [x] (mutate-in-one x new-col-name fn-of-nested)) rows))

; Data and functions for demo

(def iris
  {:species ["a" "a" "b" "b"]
   :petal-width [1 2 3 4]})

(def nested-iris
  [{:species "a" :nested [{:petal-width 1} {:petal-width 2}]}
   {:species "b" :nested [{:petal-width 3} {:petal-width 4}]}])

(defn mean
  [x]
  (/ (reduce + x) (count x)))

(defn -main
  []
  (println "Mutate out:")
  (print-table (mutate-out nested-iris :the-mean #(mean (map :petal-width %))))
  (println "Mutate in:")
  (println "Nested column corresponding to a:")
  (print-table (:nested (first (mutate-in nested-iris :inc-petal-width #(inc (:petal-width %))))))
  (println "Done."))