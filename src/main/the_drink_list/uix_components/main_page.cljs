(ns the-drink-list.uix-components.main-page
  (:require
   [the-drink-list.components.drink-form-modal :as drink-form-modal]
   [the-drink-list.components.options-nav :as options-nav]
   [the-drink-list.uix-components.context :as context]
   [the-drink-list.uix-components.drink-list :as drink-list]
   [the-drink-list.uix-components.loading-modal :as loading-modal]
   [the-drink-list.uix-components.delete-modal :as delete-modal]
   [uix.core :refer [$ defui use-context]]
   [reagent.core :as r]))

(defui main-page
  "A component which represents the main page of the app"
  []
  (let [{:keys [drink-modal-drink
                loading?
                show-delete-modal?
                show-drink-modal?
                search-term
                sort-state]}        (use-context context/app)]
    ($ :div.pt-4.pb-2
       (when loading?
         ($ loading-modal/loading-modal))
       (when show-delete-modal?
         ($ delete-modal/delete-modal))
       (when show-drink-modal?
         (r/as-element [drink-form-modal/modal {:drink drink-modal-drink}]))
       (r/as-element [options-nav/options-nav {:search-term search-term
                                               :sort-state  sort-state}])
       ($ drink-list/drink-list))))
