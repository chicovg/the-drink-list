(ns the-drink-list.app-cards
  (:require
   [devcards.core :as dc]
   [the-drink-list.uix-components.app :as app]
   [uix.core :refer [$]]))

(declare app)

(dc/defcard
  app
  ($ app/app))
