(ns the-drink-list.components.textarea-input-stories
  (:require [the-drink-list.components.form :as form]
            [reagent.core :as r]))

(def ^:export default
  #js {:title     "Components/Textarea Input"
       :component (r/reactify-component form/textarea-input)})

(defn- textarea-input-with-value
  []
  (r/with-let [value (r/atom nil)]
    [form/textarea-input {:id          :name
                          :label       "Comment"
                          :placeholder "Enter a comment"
                          :on-change   #(reset! value %)
                          :value       @value}]))

(defn ^:export Default []
  (r/as-element [textarea-input-with-value]))
