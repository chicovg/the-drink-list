(ns the-drink-list.uix-components.favorites-page-stories
  (:require
   [reagent.core :as r]
   [the-drink-list.uix-components.favorites-page :as favorites-page]
   [the-drink-list.types.drink :as drink-type]
   #_[the-drink-list.components.story-helpers :refer [with-app-state]]))

(def ^:export default
  #js {:title "Pages/Favorites Page"
       :component (r/reactify-component favorites-page/favorites-page)})

(def drinks (reduce #(assoc %1 (:id %2) %2)
                    {}
                    (drink-type/gen-drinks 100)))

;; (defn ^:export Default []
;;   (r/as-element [with-app-state
;;                  {:drinks drinks}
;;                  favorites-page/favorites-page]))
