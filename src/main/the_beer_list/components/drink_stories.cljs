(ns the-beer-list.components.drink-stories
  (:require [the-beer-list.components.drink :as drink]
            [reagent.core :as r]))

(def ^:export default
  #js {:title     "Drink Card Component"
       :component (r/reactify-component drink/card)})

(defn ^:export DrinkCard []
  (r/as-element [drink/card {:name          "Easy Ringer"
                             :maker         "Victory Brewing Company"
                             :type          "Beer"
                             :style         "Session IPA"
                             :appearance    1
                             :smell         3.219833
                             :taste         4.5
                             :taste-comment "Tastes Delicious!"}]))
