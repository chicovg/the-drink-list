(ns the-drink-list.components.text-input-stories
  (:require [the-drink-list.components.form :as form]
            [reagent.core :as r]))

(def ^:export default
  #js {:title     "Components/Text Input"
       :component (r/reactify-component form/text-input)})

(defn- text-input-with-value
  []
  (r/with-let [value (r/atom nil)]
    [form/text-input {:id          :name
                      :label       "Name"
                      :placeholder "Enter a Name"
                      :on-change   #(reset! value %)
                      :required    true
                      :value       @value}]))

(defn ^:export Default []
  (r/as-element [text-input-with-value]))
