(ns the-drink-list.components.drink-form-modal-stories
  (:require [reagent.core :as r]
            [the-drink-list.components.drink-form-modal :as drink-form-modal]
            [the-drink-list.types.drink :as drink-type]
            [the-drink-list.db :as db]))

(def ^:export default
  #js {:title     "Drink Form Modal Component"
       :component (r/reactify-component drink-form-modal/modal)})

(defn- drink-form-modal-story
  [d]
  (let [_     (db/show-drink-modal! d)
        drink @(db/drink-modal-drink)]
    [drink-form-modal/modal {:drink             drink
                             :hide-drink-modal! #(prn "hiding modal")
                             :save-drink!       #(prn "save drink" %)}]))

(defn ^:export Creating []
  (r/as-element [drink-form-modal-story nil]))

(defn ^:export Editing []
  (r/as-element [drink-form-modal-story (drink-type/gen-drink)]))
