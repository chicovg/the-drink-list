(ns the-drink-list.components.app
  (:require
   [the-drink-list.components.login-panel :as login-panel]
   [the-drink-list.components.main-page :as main-page]
   [the-drink-list.components.navbar :as navbar]
   [the-drink-list.db :as db]
   [the-drink-list.components.favorites-page :as favorites-page]))

(defn app
  []
  (let [page @(db/page)]
    [:<>
     [:header.container.is-max-desktop
      [navbar/navbar]]
     [:section.container.is-max-desktop
      (case page
        :login [login-panel/login-panel]
        :main [main-page/main-page]
        :favorites [favorites-page/favorites-page])]]))

;; TODO
;; - create state context here...
;;   OR stand it up next to db?
;; - bottom up component migration??
;;   - easier to wrap uix in reagent
;; - set up uix component alongside the regular one
;;   - lets have a components.uix folder, once done, move this to components...
