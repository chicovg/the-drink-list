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
  (r/with-let [drinks              (r/atom [])
               drink-modal-drink   (r/atom nil)
               search-term         (r/atom "")
               show-drink-modal?   (r/atom false)
               sort-state          (r/atom {:field :date
                                            :asc?  false})
               user                (r/atom nil)

               hide-drink-modal!   #(reset! show-drink-modal? false)
               set-search-term!    #(reset! search-term %)
               set-sort-state!     #(reset! sort-state %)
               set-user!           #(reset! user %)
               show-drink-modal!   #((reset! drink-modal-drink %1)
                                     (reset! show-drink-modal? true))]
    (fn [{{:keys [drinks
                  user]} :db
          sign-in        :sign-in
          sign-out       :sign-out}]
      [:section
       [:div.container.is-max-desktop.has-background-white.mt-2
        [:div.is-flex.is-justify-content-space-between.is-align-items-center
          ;; TODO can I put an icon here?
          ;; TODO make this a fixed top nav???
         [:p.card-header-title "The Drink List"]
         (when user
           [:button.button.is-ghost.is-small
            {:on-click #(sign-out)}
            [:span.icon.mr-0
             [:i.fas.fa-sign-out-alt]]
            "Logout"])]
        (if user
          [:<>
           (when @show-drink-modal?
             [drink-form-modal/modal {:drink             @drink-modal-drink
                                      :hide-drink-modal! hide-drink-modal!
                                      :show-drink-modal! show-drink-modal!}])
           [:div.pl-2.pr-2.pb-2
            [options-nav/options-nav {:set-search-term!  set-search-term!
                                      :set-sort-state!   set-sort-state!
                                      :show-drink-modal! show-drink-modal!
                                      :sort-state        @sort-state}]
            [:div.p-2]
            [drink-list/drink-list {:drinks            drinks
                                    :search-term       @search-term
                                    :show-drink-modal! show-drink-modal!
                                    :sort-state        @sort-state}]]]
          [:div.card-content
           [login-panel/login-panel {:sign-in sign-in}]])]])))
