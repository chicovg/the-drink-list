(ns the-drink-list.uix-components.app-stories
  (:require
    [reagent.core :as r]
    [the-drink-list.components.app :as app]
    [the-drink-list.components.story-helpers :refer [with-app-state]]
    [the-drink-list.types.drink :as drink-type]))

(def ^:export default
  #js {:title "Application/App"})

(defn ^:export LoggedOut []
  (r/as-element [app/app]))

(def drinks (reduce #(assoc %1 (:id %2) %2)
                    {}
                    (drink-type/gen-drinks 50)))

(defn ^:export MainPage []
  (r/as-element [with-app-state
                 {:drinks drinks
                  :page :main
                  :user {:uid "abc"}}
                 app/app]))

(defn ^:export FavoritesPage []
  (r/as-element [with-app-state
                 {:drinks drinks
                  :page :favorites
                  :user {:uid "123"}}
                 app/app]))
