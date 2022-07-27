(ns the-drink-list.uix-components.delete-modal-stories
  (:require
   [the-drink-list.uix-components.delete-modal :as delete-modal]
   [uix.core :refer [$]]))

(def ^:export default
  #js {:title "UIX Components/Delete Modal"
       :component delete-modal/delete-modal})

(defn ^:export Default []
  ($ delete-modal/delete-modal))
