(ns the-drink-list.components.drink-form-modal-stories
  (:require
   [reagent.core :as r]
   [the-drink-list.components.drink-form-modal :as drink-form-modal]
   [the-drink-list.components.story-helpers :refer [with-app-state]]
   [the-drink-list.types.drink :as drink-type]))

(def ^:export default
  #js {:title     "Components/Drink Form Modal"
       :component (r/reactify-component drink-form-modal/modal)})

(defn ^:export Creating []
  (r/as-element [drink-form-modal/modal]))

(defn ^:export Editing []
  (r/as-element [with-app-state
                 {:drink-modal {:drink (drink-type/gen-drink)}}
                 drink-form-modal/modal]))
