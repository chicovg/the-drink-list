(ns the-drink-list.uix-components.login-panel
  (:require
   [uix.core :refer [$ use-context]]
   [the-drink-list.uix-components.context :as context]
   [the-drink-list.api.firebase :as firebase]))

(defn login-panel
  []
  (let [{:keys [set-page!]} (use-context context/app)]
    ($ :div.box.mt-2
       ($ :p.mb-4
          "Log in with Google to start tracking your drinks!")
       ($ :button.button.is-link
          {:on-click #(firebase/sign-in (fn [] (set-page! :main)))}
          ($ :span.icon
             ($ :i.fab.fa-google))
          ($ :span "Login with Google")))))
