(ns the-drink-list.uix-components.drink-modal-stories
  (:require
   [the-drink-list.types.drink :as drink-type]
   [the-drink-list.uix-components.context :as context]
   [the-drink-list.uix-components.drink-modal :as drink-modal]
   [the-drink-list.uix-components.state :as state]
   [uix.core :refer [$]]))

(def ^:export default
  #js {:title     "UIX Components/Drink Modal"
       :component drink-modal/drink-modal})

(defn ^:export Creating []
  ($ (.-Provider context/app) {:value (state/use-app-state
                                       {:makers (vec drink-type/fake-brewers)
                                        :styles (vec drink-type/example-styles)
                                        :types  (vec drink-type/types)})}
     ($ drink-modal/drink-modal)))

(defn ^:export Editing []
  ($ (.-Provider context/app) {:value (state/use-app-state
                                       {:drink-modal-drink (drink-type/gen-drink)
                                        :makers (vec drink-type/fake-brewers)
                                        :styles (vec drink-type/example-styles)
                                        :types  (vec drink-type/types)})}
     ($ drink-modal/drink-modal)))
