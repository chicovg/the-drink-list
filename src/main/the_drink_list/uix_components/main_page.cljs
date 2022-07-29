(ns the-drink-list.uix-components.main-page
  (:require
   [the-drink-list.uix-components.context :as context]
   [the-drink-list.uix-components.delete-modal :as delete-modal]
   [the-drink-list.uix-components.drink-list :as drink-list]
   [the-drink-list.uix-components.drink-modal :as drink-modal]
   [the-drink-list.uix-components.loading-modal :as loading-modal]
   [the-drink-list.uix-components.options-nav :as options-nav]
   [uix.core :refer [$ defui use-context]]))

(defui main-page
  "A component which represents the main page of the app"
  []
  (let [{:keys [loading?
                show-delete-modal?
                show-drink-modal?]} (use-context context/app)]
    ($ :div.pt-4.pb-2
       (when loading?
         ($ loading-modal/loading-modal))
       (when show-delete-modal?
         ($ delete-modal/delete-modal))
       (when show-drink-modal?
         ($ drink-modal/drink-modal))
       ($ options-nav/options-nav)
       ($ drink-list/drink-list))))
