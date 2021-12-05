(ns the-beer-list.components.common)

(defn calc-overall
  [{:keys [appearance smell taste]}]
  (/ (+ appearance smell taste taste taste)
     5))
