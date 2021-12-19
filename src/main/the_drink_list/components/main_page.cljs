(ns the-drink-list.components.main-page
  (:require
   [reagent.core :as r]
   [the-drink-list.components.drink-form-modal :as drink-form-modal]
   [the-drink-list.components.drink-list :as drink-list]
   [the-drink-list.components.login-panel :as login-panel]
   [the-drink-list.components.options-nav :as options-nav]
   [the-drink-list.components.navbar :as navbar]
   [the-drink-list.components.delete-modal :as delete-modal]))

(defn main-page
  "A component which represents the main page of the app and owns the 'global' app state"
  [_]
  (r/with-let [delete-modal-drink-id  (r/atom nil)
               drink-modal-drink      (r/atom nil)
               search-term            (r/atom "")
               show-delete-modal?     (r/atom false)
               show-drink-modal?      (r/atom false)
               sort-state             (r/atom {:field :created
                                               :asc?  false})

               hide-delete-modal!         #(reset! show-delete-modal? false)
               hide-drink-modal!          #(reset! show-drink-modal? false)
               set-delete-modal-drink-id! #(reset! delete-modal-drink-id %)
               set-drink-modal-drink!     #(reset! drink-modal-drink %)
               set-search-term!           #(reset! search-term %)
               set-sort-state!            #(reset! sort-state %)
               show-delete-modal!         (fn [id]
                                            (set-delete-modal-drink-id! id)
                                            (reset! show-delete-modal? true))
               show-drink-modal!          (fn [drink]
                                            (set-drink-modal-drink! drink)
                                            (reset! show-drink-modal? true))]
    (fn [{delete-drink! :delete-drink!
          drinks        :drinks
          save-drink!   :save-drink!
          sign-in       :sign-in
          sign-out      :sign-out
          uid           :uid}]
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
                                       :hide-drink-modal!      hide-drink-modal!
                                       :save-drink!            save-drink!
                                       :set-drink-modal-drink! set-drink-modal-drink!}])
            (when @show-delete-modal?
              [delete-modal/delete-modal {:delete-drink!      delete-drink!
                                          :drink-id           @delete-modal-drink-id
                                          :hide-delete-modal! hide-delete-modal!}])
            [options-nav/options-nav {:set-search-term!  set-search-term!
                                      :set-sort-state!   set-sort-state!
                                      :show-drink-modal! show-drink-modal!
                                      :sort-state        @sort-state}]
            [drink-list/drink-list {:drinks             drinks
                                    :search-term        @search-term
                                    :show-delete-modal! show-delete-modal!
                                    :show-drink-modal!  show-drink-modal!
                                    :sort-state         @sort-state}]]
           [:div
            [login-panel/login-panel {:sign-in sign-in}]])]]])))
