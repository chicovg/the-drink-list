(ns the-beer-list.components.main-page-stories
  (:require
   [reagent.core :as r]
   [the-beer-list.components.main-page :as main-page]))

(def ^:export default
  #js {:title     "Main Page"
       :component (r/reactify-component main-page/main-page)})

(defn ^:export Default []
  (r/as-element [main-page/main-page]))
