(ns the-beer-list.core
  (:require [reagent.dom :as dom]
            [the-beer-list.components.main-page :as main-page]))

(defonce app-db {:drinks []
                 :user   nil})

(def dom-root (js/document.getElementById "app"))

(defn ^:dev/after-load start []
  (js/console.log "start")
  (dom/render [main-page/main-page @app-db] dom-root))

(defn init []
  (js/console.log "init")
  (start))

;;
;; core
;;  owns firebase client
;;     - drinks
;;     - user
;;  owns app db
;;  owns main-page
;;    owns drink-modal
;;      - drink-modal-drink
;;    owns options-nav
;;    owns drink-list
