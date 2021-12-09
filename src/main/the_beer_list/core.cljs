(ns the-beer-list.core
  (:require [reagent.dom :as dom]
            [the-beer-list.components.main-page :as main-page]))

(def dom-root (js/document.getElementById "app"))

(defn ^:dev/after-load start []
  (js/console.log "start")
  (dom/render [main-page/main-page] dom-root))

(defn init []
  (js/console.log "init")
  (start))
