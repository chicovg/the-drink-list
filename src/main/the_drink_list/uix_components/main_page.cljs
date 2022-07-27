(ns the-drink-list.uix-components.main-page
  (:require
   [the-drink-list.components.drink-form-modal :as drink-form-modal]
   [the-drink-list.components.options-nav :as options-nav]
   [the-drink-list.uix-components.context :as context]
   [the-drink-list.uix-components.drink-list :as drink-list]
   [the-drink-list.uix-components.loading-modal :as loading-modal]
   [the-drink-list.uix-components.delete-modal :as delete-modal]
   [uix.core :refer [$ defui use-state]]
   [reagent.core :as r]))

;; TODO
;; should drinks/loading? come from app context?
;; any benefit to moving everything up to app context?

(defui main-page
  "A component which represents the main page of the app"
  [{:keys [drinks loading?]}]
  (let [[delete-modal-drink-id set-delete-modal-drink-id!] (use-state nil)
        [drink-modal-drink set-drink-modal-drink!]         (use-state nil)
        [search-term set-search-term!]                     (use-state nil)
        [show-delete-modal? set-show-delete-modal!]        (use-state false)
        [show-drink-modal? set-show-drink-modal!]          (use-state false)
        [sort-state set-sort-state!]                       (use-state {:field :created
                                                                       :asc?  false})

        hide-delete-modal! (fn [] (set-show-delete-modal! false))
        show-delete-modal! (fn [id]
                             (set-delete-modal-drink-id! id)
                             (set-show-delete-modal! true))
        show-drink-modal!  (fn [drink]
                             (set-drink-modal-drink! drink)
                             (set-show-drink-modal! true))]
    ($ (.-Provider context/main-page) {:value {:delete-modal-drink-id delete-modal-drink-id
                                               :drink-modal-drink     drink-modal-drink
                                               :hide-delete-modal!    hide-delete-modal!
                                               :search-term           search-term
                                               :set-search-term!      set-search-term!
                                               :show-delete-modal!    show-delete-modal!
                                               :show-drink-modal!     show-drink-modal!
                                               :sort-state            sort-state
                                               :set-sort-state!       set-sort-state!}}
       ($ :div.pt-4.pb-2
          (when loading?
            ($ loading-modal/loading-modal))
          (when show-delete-modal?
            ($ delete-modal/delete-modal))
          (when show-drink-modal?
            (r/as-element [drink-form-modal/modal {:drink drink-modal-drink}]))
          (r/as-element [options-nav/options-nav {:search-term search-term
                                                  :sort-state  sort-state}])
          ($ drink-list/drink-list {:drinks drinks})))))
