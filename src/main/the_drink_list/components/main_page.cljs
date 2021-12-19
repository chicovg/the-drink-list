(ns the-drink-list.components.main-page
  (:require
   [the-drink-list.db :as db]
   [the-drink-list.components.drink-form-modal :as drink-form-modal]
   [the-drink-list.components.drink-list :as drink-list]
   [the-drink-list.components.login-panel :as login-panel]
   [the-drink-list.components.options-nav :as options-nav]
   [the-drink-list.components.navbar :as navbar]
   [the-drink-list.components.delete-modal :as delete-modal]))

(defn main-page
  "A component which represents the main page of the app and owns the 'global' app state"
  [{delete-drink! :delete-drink!
    drinks        :drinks
    save-drink!   :save-drink!
    sign-in       :sign-in
    sign-out      :sign-out
    uid           :uid}]
  (let [delete-modal-drink-id (db/delete-modal-drink-id)
        drink-modal-drink     (db/drink-modal-drink)
        search-term           (db/search-term)
        show-delete-modal?    (db/show-delete-modal?)
        show-drink-modal?     (db/show-drink-modal?)
        sort-state            (db/sort-state)]
    [:<>
     [:header.container.is-max-desktop
        ;; TODO can I put an icon here?
      [navbar/navbar {:sign-out sign-out
                      :uid      uid}]]
     [:section
      [:div.container.is-max-desktop
       (if uid
         [:div.pt-4.pb-2
          (when @show-drink-modal?
            [drink-form-modal/modal {:drink                  @drink-modal-drink
                                     :save-drink!            save-drink!}])
          (when @show-delete-modal?
            [delete-modal/delete-modal {:delete-drink!      delete-drink!
                                        :drink-id           @delete-modal-drink-id}])
          [options-nav/options-nav {:sort-state @sort-state}]
          [drink-list/drink-list {:drinks             drinks
                                  :search-term        @search-term
                                  :sort-state         @sort-state}]]
         [:div
          [login-panel/login-panel {:sign-in sign-in}]])]]]))
