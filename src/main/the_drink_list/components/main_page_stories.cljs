(ns the-drink-list.components.main-page-stories
  (:require
   [reagent.core :as r]
   [the-drink-list.components.main-page :as main-page]
   [the-drink-list.types.drink :as drink-type]
   [the-drink-list.db :as db]))

(def ^:export default
  #js {:title     "Main Page"
       :component (r/reactify-component main-page/main-page)})

(defn main-page-story
  [{drinks :drinks
    uid    :uid}]
  (r/create-class
   {:component-did-mount
    (fn []
      (db/set-drinks! drinks)
      (db/set-user!   {:uid uid}))
    :component-will-unmount
    (fn []
      (db/set-drinks! nil)
      (db/set-user! nil))
    :reagent-render
    (fn [_]
      [main-page/main-page])}))

(defn ^:export Default []
  (r/as-element [main-page-story {:drinks (reduce #(assoc %1 (:id %2) %2)
                                                  {}
                                                  (drink-type/gen-drinks 20))
                                  :uid    "abc"}]))

(defn ^:export LoggedOut []
  (r/as-element [main-page-story {}]))
