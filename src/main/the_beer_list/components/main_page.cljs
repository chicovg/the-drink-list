(ns the-beer-list.components.main-page
  (:require
   [reagent.core :as r]
   [the-beer-list.components.drink-list :as drink-list]
   [the-beer-list.components.options-nav :as options-nav]
   [the-beer-list.components.login-panel :as login-panel]))

(defn main-page
  "A component which represents the main page of the app and owns the 'global' app state"
  []
  (r/with-let [drinks          (r/atom [])
               search-term     (r/atom "")
               sort-state      (r/atom {:field :date
                                        :asc?  false})
               user            (r/atom nil)
               set-search-term #(reset! search-term %)
               set-sort-state  #(reset! sort-state %)
               set-user        #(reset! user %)]
    (fn []
      [:section
       [:div.container.is-max-desktop
        [:div.card.mt-4
         [:div.card-header
          ;; TODO can I put an icon here?
          [:p.card-header-title
           "The Drink List"]]
         (if :user
           [:div.card-content
            [options-nav/options-nav {:set-search-term set-search-term
                                      :set-sort-state  set-sort-state
                                      :sort-state      @sort-state}]
            [:div.p-2]
            [drink-list/drink-list {:drinks      @drinks
                                    :search-term @search-term
                                    :sort-state  @sort-state}]]
           [:div.card-content
            [login-panel/login-panel {:set-user set-user}]])]]])))
