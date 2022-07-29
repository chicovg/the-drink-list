(ns the-drink-list.core
  (:require #_[reagent.dom :as dom]
            [the-drink-list.api.firebase :as firebase]
            [the-drink-list.db :as db]
            [the-drink-list.uix-components.app :as app]
            [uix.core :refer [$]]
            [uix.dom :as dom]))

(def dom-root (js/document.getElementById "app"))

;; (defn ^:dev/after-load start []
;;   (js/console.log "start")
;;   (db/set-loading! true)
;;   (firebase/listen-to-auth db/set-user-and-load-drinks!)
;;   (dom/render [app/app] dom-root))

;; (defn init []
;;   (js/console.log "init")
;;   (start))

(defn init []
  (js/console.log "init")
  (dom/render ($ app/app) dom-root))
