(ns the-drink-list.uix-components.autocomplete-stories
  (:require [the-drink-list.components.autocomplete :as autocomplete]
            [the-drink-list.types.drink :as drink]
            [reagent.core :as r]))

(def ^:export default
  #js {:title     "Components/Autocomplete"
       :component (r/reactify-component autocomplete/autocomplete)})

(defn controlled-autocomplete
  []
  (r/with-let [value (r/atom nil)]
    (fn []
      [autocomplete/autocomplete {:id          :makers
                                  :label       "Brewery"
                                  :on-change   #(reset! value %)
                                  :suggestions (vec drink/fake-brewers)
                                  :value       @value}])))

(defn ^:export Default []
  (r/as-element [controlled-autocomplete]))
