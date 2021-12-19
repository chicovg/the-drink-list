(ns the-drink-list.components.login-panel
  (:require
    [the-drink-list.db :as db]))

(defn login-panel
  []
  [:div.box.mt-2
   [:p.mb-4
    "Log in with Google to start tracking your drinks!"]
   [:button.button.is-link
    {:on-click #(db/sign-in)}
    [:span.icon
     [:i.fab.fa-google]]
    [:span
     "Login with Google"]]])
