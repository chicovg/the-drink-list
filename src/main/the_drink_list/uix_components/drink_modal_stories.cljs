(ns the-drink-list.uix-components.drink-modal-stories
  (:require
   [reagent.core :as r]
   [the-drink-list.uix-components.drink-modal :as drink-modal]
   [the-drink-list.types.drink :as drink-type]
   [uix.core :refer [$]]
   [the-drink-list.uix-components.context :as context]))

(def ^:export default
  #js {:title     "UIX Components/Drink Form Modal"
       :component drink-modal/drink-modal})

(defn ^:export Creating []
  ($ (.-Provider context/app) {:value (context/use-app-state)}
     ($ drink-modal/drink-modal)))

(defn ^:export Editing []
  ($ (.-Provider context/app) {:value (context/use-app-state
                                       {:drink-modal-drink (drink-type/gen-drink)})}
     ($ drink-modal/drink-modal)))
