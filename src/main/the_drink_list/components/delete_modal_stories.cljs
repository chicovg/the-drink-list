(ns the-drink-list.components.delete-modal-stories
  (:require
   [reagent.core :as r]
   [the-drink-list.components.delete-modal :as delete-modal]))

(def ^:export default
  #js {:title "Delete Modal"
       :component (r/reactify-component delete-modal/delete-modal)})

(defn ^:export Default []
  (r/as-element [delete-modal/delete-modal]))
