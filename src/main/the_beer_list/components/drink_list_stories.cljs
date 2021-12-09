(ns the-beer-list.components.drink-list-stories
  (:require [reagent.core :as r]
            [the-beer-list.components.drink-list :as drink-list]
            [the-beer-list.types.drink :as drink-type]))

(def ^:export default
  #js {:title     "Drink List Component"
       :component (r/reactify-component drink-list/drink-list)})

(defn ^:export Default []
  (r/as-element [drink-list/drink-list {:drinks      (drink-type/gen-drinks 20)
                                        :search-term ""
                                        :sort-state  {:asc?  false
                                                      :field :date}}]))
