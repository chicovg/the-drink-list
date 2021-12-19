(ns the-drink-list.components.main-page-stories
  (:require
   [reagent.core :as r]
   [the-drink-list.components.main-page :as main-page]
   [the-drink-list.types.drink :as drink-type]))

(def ^:export default
  #js {:title     "Main Page"
       :component (r/reactify-component main-page/main-page)})

(defn ^:export Default []
  (r/as-element [main-page/main-page {:drinks (drink-type/gen-drinks 20)
                                      :uid    "abc"}]))

(defn ^:export LoggedOut []
  (r/as-element [main-page/main-page {}]))
