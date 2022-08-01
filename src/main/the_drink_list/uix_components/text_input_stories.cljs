(ns the-drink-list.uix-components.text-input-stories
  (:require
   [the-drink-list.uix-components.text-input :as text-input]
   [uix.core :refer [$ use-state]]))

(def ^:export default
  #js {:title     "UIX Components/Text Input"
       :component text-input/text-input})

(defn ^:export Default []
  (let [[value set-value!] (use-state nil)]
    ($ text-input/text-input {:id          :name
                              :label       "Name"
                              :placeholder "Enter a Name"
                              :on-change   set-value!
                              :required    true
                              :value       value})))
