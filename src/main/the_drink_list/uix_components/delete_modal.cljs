(ns the-drink-list.uix-components.delete-modal
  (:require [the-drink-list.db :as db]
            [uix.core :refer [$ defui use-context]]
            [the-drink-list.uix-components.context :as context]))

(defui delete-modal
  []
  (let [{:keys [delete-modal-drink-id
                hide-delete-modal!]} (use-context context/main-page)]
    ($ :div.modal.is-active
       ($ :div.modal-background)
       ($ :div.modal-content
          ($ :article.message.is-danger
             ($ :div.message-header
                ($ :p "Confirm"))
             ($ :div.message-body
                ($ :p.has-text-black "Are you sure that you want to delete?")
                ($ :div.pb-4)
                ($ :button.button.is-danger.mr-2
                   ;; TODO this type of thing should probably be in app context...
                   {:on-click #(db/delete-drink! delete-modal-drink-id)}
                   "Yes")
                ($ :button.button.is-white
                   {:on-click hide-delete-modal!}
                   "Cancel")))))))
