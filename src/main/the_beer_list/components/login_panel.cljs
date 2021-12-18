(ns the-beer-list.components.login-panel)

(defn login-panel
  [{:keys [sign-in]}]
  [:div.box.mt-2
   [:p.mb-4
    "Log in with Google to start tracking your drinks!"]
   [:button.button.is-link
    {:on-click #(sign-in)}
    [:span.icon
     [:i.fab.fa-google]]
    [:span
     "Login with Google"]]])
