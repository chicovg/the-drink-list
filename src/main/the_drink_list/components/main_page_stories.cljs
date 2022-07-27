(ns the-drink-list.components.main-page-stories
  (:require
   [reagent.core :as r]
   [the-drink-list.components.main-page :as main-page]
   [the-drink-list.components.story-helpers :refer [with-app-state]]
   [the-drink-list.types.drink :as drink-type]))

(def ^:export default
  #js {:title     "Pages/Main Page"
       :component (r/reactify-component main-page/main-page)})

(def drinks (reduce #(assoc %1 (:id %2) %2)
                    {}
                    (drink-type/gen-drinks 20)))

(defn ^:export Default []
  (r/as-element [with-app-state
                 {:drinks drinks}
                 main-page/main-page]))

(defn ^:export Loading []
  (r/as-element [with-app-state
                 {:loading? true}
                 main-page/main-page]))
