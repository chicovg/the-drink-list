(ns the-drink-list.uix-components.navbar
  (:require
   [the-drink-list.uix-components.context :as context]
   [uix.core :refer [$ defui use-context use-state]]
   [the-drink-list.api.firebase :as firebase]))

(defui navbar
  [{:keys [user]}]
  (let [{:keys [page set-page!]}    (use-context context/app)
        [show-menu? set-show-menu!] (use-state false)]
    ($ :nav.navbar.is-primary
       ($ :div.navbar-brand
          ($ :div.navbar-item
             ($ :p.is-size-4.has-text-weight-semi-bold.ml-2
                "The Drink List"))
          (when user
            ($ :a.navbar-burger {:aria-expanded "false"
                                 :aria-label    "menu"
                                 :class         (when show-menu? "is-active")
                                 :data-target   "nav-menu"
                                 :on-click      #(set-show-menu! (not show-menu?))
                                 :role          "button"}
               ($ :span {:aria-hidden "true"})
               ($ :span {:aria-hidden "true"})
               ($ :span {:aria-hidden "true"}))))
       (when user
         ($ :div#nav-menu.navbar-menu
            {:class (when show-menu? "is-active")}
            ($ :div.navbar-end
               ($ :a.navbar-item
                  {:class    (when (= page :main) "is-active")
                   :on-click #(set-page! :main)}
                  "Home")
               ($ :a.navbar-item
                  {:class    (when (= page :favorites) "is-active")
                   :on-click #(set-page! :favorites)}
                  "Favorites")
               ($ :a.navbar-item
                  {:class    (when (= page :favorites) "is-active")
                   :on-click #(firebase/sign-out)}
                  "Sign Out")))))))
