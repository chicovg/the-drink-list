(ns the-drink-list.uix-components.drink-modal-stories
  (:require
   [the-drink-list.types.drink :as drink-type]
   [the-drink-list.uix-components.context :as context]
   [the-drink-list.uix-components.drink-modal :as drink-modal]
   [the-drink-list.uix-components.state :as state]
   [uix.core :refer [$]]))

(def ^:export default
  #js {:title     "UIX Components/Drink Form Modal"
       :component drink-modal/drink-modal})

(defn ^:export Creating []
  ($ (.-Provider context/app) {:value (state/use-app-state)}
     ($ drink-modal/drink-modal)))

(defn ^:export Editing []
  ($ (.-Provider context/app) {:value (state/use-app-state
                                       {:drink-modal-drink (drink-type/gen-drink)})}
     ($ drink-modal/drink-modal)))
