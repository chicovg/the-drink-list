(ns the-drink-list.uix-components.textarea-input
  (:require
   [uix.core :refer [$ defui]]))

(defui textarea-input
  [{:keys [id label on-change value] :as props}]
    ($ :div.field
       ($ :label.label {:for id} label)
       ($ :div.control
          ($ :textarea.textarea (assoc props
                                       :on-change #(on-change (-> % .-target .-value))
                                       :value     (or value ""))))))
