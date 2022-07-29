(ns the-drink-list.uix-components.app
  (:require
   [the-drink-list.uix-components.state :as state]
   [reagent.core :as r]
   [the-drink-list.uix-components.context :as context]
   [the-drink-list.uix-components.favorites-page :as favorites-page]
   [the-drink-list.uix-components.login-panel :as login-panel]
   [the-drink-list.uix-components.main-page :as main-page]
   [the-drink-list.uix-components.navbar :as navbar]
   [uix.core :refer [$ defui use-effect use-state]]
   [the-drink-list.api.firebase :as firebase]))

(defui app
  []
  (let [[user set-user!]         (use-state nil)
        {:keys [set-drinks-map!
                set-loading!]
         :as app-state}          (state/use-app-state)]

    (use-effect
     (fn []
       (firebase/listen-to-auth set-user!))
     [])

    (use-effect
     (fn []
       (if user
         (firebase/get-drinks {:user         user
                               :set-loading! set-loading!
                               :on-success   set-drinks-map!
                               :on-error     #(js/console.error %)})
         (set-drinks-map! nil)))
     [user])

    ($ (.-Provider context/app) {:value (assoc app-state :user user)}
       ($ :<>
          ($ :header.container.is-max-desktop
             ($ navbar/navbar {:user user}))
          ($ :section.container.is-max-desktop
             (if-not user
               ($ login-panel/login-panel)
               (case (:page app-state)
                 :main      ($ main-page/main-page)
                 :favorites (r/as-element [favorites-page/favorites-page]))))))))

