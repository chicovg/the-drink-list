(ns the-beer-list.components.common)

(defn rating-class
  [value]
  (condp > value
    2 "rating__lt-2"
    3 "rating__lt-3"
    4 "rating__lt-4"
    "rating__lt-5"))
