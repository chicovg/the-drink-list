(ns the-drink-list.uix-components.loading-modal
  (:require [uix.core :refer [$ defui]]))

(defui loading-modal
  []
  ($ :div.modal.is-active
     ($ :div.modal-background)
     ($ :div.modal-content
        ($ :article.message.is-info
           ($ :div.message-header
              ($ :p "Loading..."))
           ($ :div.message-body
              ($ :progress.progress.is-info {:max 100}))))))
