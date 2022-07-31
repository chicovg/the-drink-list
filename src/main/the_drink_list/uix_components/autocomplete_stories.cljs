(ns the-drink-list.uix-components.autocomplete-stories
  (:require
   [the-drink-list.uix-components.autocomplete :as autocomplete]
   [the-drink-list.types.drink :as drink]
   [uix.core :refer [$ use-state]]))

(def ^:export default
  #js {:title     "UIX Components/Autocomplete"
       :component autocomplete/autocomplete})

(defn ^:export Default []
  (let [[value set-value!] (use-state nil)]
    ($ autocomplete/autocomplete {:id          :makers
                                  :label       "Brewery"
                                  :on-change   set-value!
                                  :suggestions (vec drink/fake-brewers)
                                  :value       value})))
