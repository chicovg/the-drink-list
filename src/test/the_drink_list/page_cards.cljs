(ns the-drink-list.page-cards
  (:require
   [devcards.core :as dc]
   [the-drink-list.types.drink :as drink-type]
   [the-drink-list.uix-components.context :as context]
   [the-drink-list.uix-components.favorites-page :as favorites-page]
   [the-drink-list.uix-components.login-panel :as login-panel]
   [the-drink-list.uix-components.main-page :as main-page]
   [the-drink-list.uix-components.state :as state]
   [uix.core :refer [$ defui]]))

(declare login-panel)

(dc/defcard
  login-panel
  ($ login-panel/login-panel))

(def drinks (drink-type/gen-drinks 5))

(defui main-preview []
  ($ (.-Provider context/app)
     {:value (state/use-app-state {:drinks   drinks
                                   :loading? false})}
     ($ main-page/main-page {:drinks drinks})))

(declare main)

(dc/defcard
  main
  ($ main-preview))

(defui favorites-preview []
  ($ (.-Provider context/app)
     {:value (state/use-app-state {:drinks   (drink-type/gen-drinks 100)
                                   :loading? false})}
     ($ favorites-page/favorites-page)))

(declare favorites)

(dc/defcard
  favorites
  ($ favorites-preview))
