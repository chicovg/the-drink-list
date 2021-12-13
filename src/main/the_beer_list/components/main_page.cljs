(ns the-beer-list.components.main-page
  (:require
   [reagent.core :as r]
   [the-beer-list.components.drink-form-modal :as drink-form-modal]
   [the-beer-list.components.drink-list :as drink-list]
   [the-beer-list.components.options-nav :as options-nav]
   [the-beer-list.components.login-panel :as login-panel]))

(defn main-page
  "A component which represents the main page of the app and owns the 'global' app state"
  [_]
  (r/with-let [drink-modal-drink      (r/atom nil)
               search-term            (r/atom "")
               show-drink-modal?      (r/atom false)
               sort-state             (r/atom {:field :date
                                               :asc?  false})
               hide-drink-modal!      #(reset! show-drink-modal? false)
               set-drink-modal-drink! #(reset! drink-modal-drink %)
               set-search-term!       #(reset! search-term %)
               set-sort-state!        #(reset! sort-state %)
               show-drink-modal!      #((set-drink-modal-drink! %1)
                                        (reset! show-drink-modal? true))]
    (fn [{drinks      :drinks
          save-drink! :save-drink!
          sign-in     :sign-in
          sign-out    :sign-out
          uid         :uid}]
      [:<>
       [:header
        [:div.container.is-max-desktop.is-flex.is-justify-content-space-between.is-align-items-center.has-background-primary-light.mt-2
          ;; TODO can I put an icon here?
          ;; TODO make this a fixed top nav???
         [:p.card-header-title "The Drink List"]
         (when uid
           [:button.button.is-ghost.is-small
            {:on-click #(sign-out)}
            [:span.icon.mr-0
             [:i.fas.fa-sign-out-alt]]
            "Logout"])]]
       [:section
        [:div.container.is-max-desktop.has-background-white
         (if uid
           [:div.pt-2.pb-2
            (when @show-drink-modal?
              [drink-form-modal/modal {:drink                  @drink-modal-drink
                                       :hide-drink-modal!      hide-drink-modal!
                                       :save-drink!            save-drink!
                                       :set-drink-modal-drink! set-drink-modal-drink!}])
            [:div.pl-4.pr-4.pb-2
             [options-nav/options-nav {:set-search-term!  set-search-term!
                                       :set-sort-state!   set-sort-state!
                                       :show-drink-modal! show-drink-modal!
                                       :sort-state        @sort-state}]
             [drink-list/drink-list {:drinks            drinks
                                     :search-term       @search-term
                                     :show-drink-modal! show-drink-modal!
                                     :sort-state        @sort-state}]]]
           [:div
            [login-panel/login-panel {:sign-in sign-in}]])]]])))
