(ns the-drink-list.uix-components.favorites-page-stories
  (:require
   [the-drink-list.types.drink :as drink-type]
   [the-drink-list.uix-components.context :as context]
   [the-drink-list.uix-components.favorites-page :as favorites-page]
   [the-drink-list.uix-components.state :as state]
   [uix.core :refer [$]]))

(def ^:export default
  #js {:title "UIX Pages/Favorites Page"
       :component favorites-page/favorites-page})

(defn ^:export Default []
  ($ (.-Provider context/app)
     {:value (state/use-app-state {:drinks   (drink-type/gen-drinks 100)
                                   :loading? false})}
     ($ favorites-page/favorites-page)))
