(ns the-beer-list.components.drink-stories
  (:require [the-beer-list.components.drink :as drink]
            [the-beer-list.types.drink :as drink-type]
            [reagent.core :as r]))

(def ^:export default
  #js {:title     "Drink Card Component"
       :component (r/reactify-component drink/card)})

(defn ^:export Displaying []
  (r/as-element [drink/card (drink-type/gen-drink)]))
