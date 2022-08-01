(ns the-drink-list.uix-components.app-stories
  (:require
   [the-drink-list.uix-components.app :as app]
   [uix.core :refer [$]]))

(def ^:export default
  #js {:title "Application/App"})


(defn ^:export MainPage []
  ($ app/app))

#_(defn ^:export FavoritesPage []
  (r/as-element [with-app-state
                 {:drinks drinks
                  :page :favorites
                  :user {:uid "123"}}
                 app/app]))
