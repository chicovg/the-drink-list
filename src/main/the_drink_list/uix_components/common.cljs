(ns the-drink-list.uix-components.common)

(defn rating-class
  [value]
  (condp > value
    2 "lt-2"
    3 "lt-3"
    4 "lt-4"
    "lt-5"))
