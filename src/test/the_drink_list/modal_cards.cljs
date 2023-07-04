(ns the-drink-list.modal-cards
  (:require
   [devcards.core :as dc]
   [the-drink-list.uix-components.context :as context]
   [the-drink-list.uix-components.delete-modal :as delete-modal]
   [uix.core :refer [$ create-context defui use-state]]
   [the-drink-list.uix-components.drink-modal :as drink-modal]
   [the-drink-list.types.drink :as drink]))

(defui show-modal-button
  [{:keys [on-click]}]
  ($ :button.button.is-primary {:on-click on-click}
     "Show"))

(defui delete-modal-preview
  []
  (let [[show? set-show!] (use-state false)]
    ($ (.-Provider context/app) {:value {:hide-delete-modal! #(set-show! false)}}
       (if show?
         ($ delete-modal/delete-modal)
         ($ show-modal-button {:on-click #(set-show! true)})))))

(declare delete-modal)

(dc/defcard
  delete-modal
  ($ delete-modal-preview))

(defui drink-modal-preview
  [opts]
  (let [[show? set-show!] (use-state false)]
    ($ (.-Provider context/app) {:value (merge opts {:hide-drink-modal! #(set-show! false)})}
       (if show?
         ($ drink-modal/drink-modal)
         ($ show-modal-button {:on-click #(set-show! true)})))))

(declare drink-modal--new)

(dc/defcard
  drink-modal--new
  ($ drink-modal-preview))

(declare drink-modal--edit)

(dc/defcard
  drink-modal--edit
  ($ drink-modal-preview {:drink-modal-drink (drink/gen-drink)}))
