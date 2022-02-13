(ns the-drink-list.components.story-helpers
  (:require
    [the-drink-list.db :as db]
    [reagent.core :as r]))

(defn with-app-state
  [state story-fn]
  (r/create-class
   {:component-did-mount
    (fn []
      (db/reset-app-db! (merge db/default-state state)))
    :component-will-unmount
    (fn []
      (db/reset-app-db!))
    :reagent-render
    story-fn}))
