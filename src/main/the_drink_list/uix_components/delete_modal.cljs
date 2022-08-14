(ns the-drink-list.uix-components.delete-modal
  (:require [uix.core :refer [$ defui use-context]]
            [the-drink-list.uix-components.context :as context]
            [the-drink-list.api.firebase :as firebase]))

(defui delete-modal
  []
  (let [{:keys [delete-modal-drink-id
                hide-delete-modal!
                remove-drink!
                set-loading!
                user]}                (use-context context/app)]
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
                   {:on-click #(firebase/delete-drink! {:user         user
                                                        :drink-id     delete-modal-drink-id
                                                        :set-loading! set-loading!
                                                        :on-success   (comp hide-delete-modal! remove-drink!)
                                                        :on-error     js/console.error})}
                   "Yes")
                ($ :button.button.is-white
                   {:on-click hide-delete-modal!}
                   "Cancel")))))))
