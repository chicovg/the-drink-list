(ns the-drink-list.core
  (:require [reagent.core :as r]
            [reagent.dom :as dom]
            [the-drink-list.api.firebase :as firebase]
            [the-drink-list.components.main-page :as main-page]
            [the-drink-list.db :as db]))

(def dom-root (js/document.getElementById "app"))

(defn ^:dev/after-load start []
  (js/console.log "start")
  (firebase/listen-to-auth db/set-user!)
  (dom/render [main-page/main-page] dom-root))

(defn init []
  (js/console.log "init")
  (start))
