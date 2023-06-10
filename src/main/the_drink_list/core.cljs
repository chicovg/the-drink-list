(ns the-drink-list.core
  (:require
   [the-drink-list.uix-components.app :as app]
   [uix.core :refer [$]]
   [uix.dom :as dom]))

(def dom-root (js/document.getElementById "app"))

(defn init []
  (dom/render ($ app/app) dom-root))
