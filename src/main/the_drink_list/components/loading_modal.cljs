(ns the-drink-list.components.loading-modal)

(defn loading-modal
  []
  [:div.modal.is-active
   [:div.modal-background]
   [:div.modal-content
    [:article.message.is-info
     [:div.message-header
      [:p "Loading..."]]
     [:div.message-body
      [:progress.progress.is-info {:max 100}]]]]])
