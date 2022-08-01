(ns the-drink-list.uix-components.slider-input
  (:require
   [uix.core :refer [$ defui]]))

(defui slider-input
  [{:keys [id label on-change value] :as props}]
  ($ :div.field
     ($ :label.label {:for id} label)
     ($ :input.slider.has-output.is-primary.is-full-width
        (assoc props
               :id        id
               :on-change #(on-change (some-> %
                                              .-target
                                              .-value
                                              js/Number))
               :type      "range"
               :value     (str value)))
     ($ :output {:for id} value)))
