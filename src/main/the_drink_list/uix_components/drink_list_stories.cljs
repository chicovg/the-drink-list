(ns the-drink-list.uix-components.drink-list-stories
  (:require [the-drink-list.uix-components.drink-list :as drink-list]
            [the-drink-list.types.drink :as drink-type]
            [uix.core :refer [$]]))

(def ^:export default
  #js {:title     "UIX Components/Drink List"
       :component drink-list/drink-list})

(defn ^:export Default []
  ($ drink-list/drink-list {:drinks      (drink-type/gen-drinks 20)
                            :search-term ""
                            :sort-state  {:asc?  false
                                          :field :date}}))
