(ns the-beer-list.components.login-panel)

(defn login-panel
  [{:keys [set-user]}]
   ;; TODO: maybe I just have the whole app in this card and use the card title as header...
  [:<>
   [:p.pb-4
    "Log in with Google to start tracking your drinks!"]
   [:button.button.is-link
    [:span.icon
     [:i.fab.fa-google]]
    [:span
     "Login with Google"]]])
