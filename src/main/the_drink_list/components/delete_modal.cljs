(ns the-drink-list.components.delete-modal
  (:require [the-drink-list.db :as db]))

(defn delete-modal
  [{drink-id :drink-id}]
  [:div.modal.is-active
   [:div.modal-background]
   [:div.modal-content
    [:article.message.is-danger
     [:div.message-header
      [:p "Confirm"]]
     [:div.message-body
      [:p.has-text-black "Are you sure that you want to delete?"]
      [:div.pb-4]
      [:button.button.is-danger.mr-2
       {:on-click #(db/delete-drink! drink-id)}
       "Yes"]
      [:button.button.is-white
       {:on-click #(db/hide-delete-modal!)}
       "Cancel"]]]]])
