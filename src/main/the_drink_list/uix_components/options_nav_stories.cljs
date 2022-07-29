(ns the-drink-list.uix-components.options-nav-stories
  (:require
   [reagent.core :as r]
   [the-drink-list.components.options-nav :as options-nav]
   [the-drink-list.db :as db]))

(def ^:export default
  #js {:title     "Components/Options Nav"
       :component (r/reactify-component options-nav/options-nav)})

(defn options-nav-story
  []
  (let [search-term @(db/search-term)
        sort-state  @(db/sort-state)]
    [options-nav/options-nav {:search-term search-term
                              :sort-state  sort-state}]))

(defn ^:export Default []
  (r/as-element [options-nav-story]))
