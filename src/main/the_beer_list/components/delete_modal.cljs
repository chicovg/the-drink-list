(ns the-beer-list.components.delete-modal)

(defn delete-modal
  [{:keys [delete-drink!
           drink-id
           hide-delete-modal!]}]
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
       {:on-click (fn [_]
                    (delete-drink!
                     drink-id
                     hide-delete-modal!
                     ;; TODO show an error here
                     #(prn %)))}
       "Yes"]
      [:button.button.is-white
       {:on-click #(hide-delete-modal!)}
       "Cancel"]]]]])
