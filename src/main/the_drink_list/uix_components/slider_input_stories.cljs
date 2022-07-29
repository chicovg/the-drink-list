(ns the-drink-list.uix-components.slider-input-stories
  (:require [the-drink-list.components.form :as form]
            [reagent.core :as r]))

(def ^:export default
  #js {:title     "Components/Slider Input"
       :component (r/reactify-component form/slider-input)})

(defn- slider-input-with-value
  []
  (r/with-let [value (r/atom 3)]
    (fn []
      [form/slider-input {:id          :rating
                          :min         "1"
                          :max         "5"
                          :label       "Rating"
                          :on-change   #(reset! value %)
                          :step        "0.1"
                          :value       @value}])))

(defn ^:export Default []
  (r/as-element [slider-input-with-value]))
