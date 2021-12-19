(ns the-drink-list.components.navbar
  (:require
    [the-drink-list.db :as db]))

(defn navbar
  [{uid :uid}]
  [:nav.navbar.is-primary
   [:div.navbar-brand
    [:div.navbar-item
     [:p.is-size-4.has-text-weight-semi-bold.ml-2
      "The Drink List"]]]
   (when uid
     [:div.navbar-end
      [:div.navbar-item
       [:button.button.is-light.is-small
        {:on-click #(db/sign-out)}
        [:span.icon.mr-0
         [:i.fas.fa-sign-out-alt]]
        "Logout"]]])])
