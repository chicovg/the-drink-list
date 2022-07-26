(ns the-drink-list.uix-components.logged-at
  (:require [uix.core :refer [$ defui]]))

(defn- quantity-and-message
  [millis divisor base]
  (let [quantity (int (/ millis divisor))
        message  (str base (when (> quantity 1) "s") " ago")]
    (str quantity " " message)))

(defn- duration-message
  [diff-in-millis]
  (cond
    (>= diff-in-millis (* 1000 60 60 24 365))
    (quantity-and-message diff-in-millis (* 1000 60 60 24 365) "year")

    (>= diff-in-millis (* 1000 60 60 24 30))
    (quantity-and-message diff-in-millis (* 1000 60 60 24 30) "month")

    (>= diff-in-millis (* 1000 60 60 24 7))
    (quantity-and-message diff-in-millis (* 1000 60 60 24 7) "week")

    (> diff-in-millis (* 1000 60 60 24))
    (quantity-and-message diff-in-millis (* 1000 60 60 24) "day")

    (>= diff-in-millis (* 1000 60 60))
    (quantity-and-message diff-in-millis (* 1000 60 60) "hour")

    (>= diff-in-millis (* 1000 60))
    (quantity-and-message diff-in-millis (* 1000 60) "min")

    :else
    "<1 min"))

(defui logged-at
  [{:keys [date]}]
  (let [diff-in-millis (- (js/Date.) date)
        message        (duration-message diff-in-millis)]
    ($ :p.is-size-8.is-italic message)))
