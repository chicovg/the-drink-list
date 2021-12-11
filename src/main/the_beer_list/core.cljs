(ns the-beer-list.core
  (:require [reagent.core :as r]
            [reagent.dom :as dom]
            [the-beer-list.api.firebase :as firebase]
            [the-beer-list.components.main-page :as main-page]))

(def dom-root (js/document.getElementById "app"))

(defn- root
  []
  (r/with-let [app-db   (r/atom {:drinks nil
                                 :user   nil})
               set-user #(swap! app-db assoc :user %)
               _        (firebase/listen-to-auth set-user)
               sign-in  #(firebase/sign-in set-user)
               sign-out #(firebase/sign-out set-user)]
    (fn []
      (let [_ (when (and (:user @app-db) (nil? (:drinks @app-db)))
                (firebase/get-drinks
                 (.-uid (:user @app-db))
                 #(swap! app-db assoc :drinks %)))]
        (prn @app-db)
        [main-page/main-page {:db       @app-db
                              :sign-in  sign-in
                              :sign-out sign-out}]))))

(defn ^:dev/after-load start []
  (js/console.log "start")
  (dom/render [root] dom-root))

(defn init []
  (js/console.log "init")
  (start))

;; Where do I need the firebase client?
;; core
;;   - fetch drinks
;;   main
;;      login-panel - login
;;      drink-form-modal - create/update drink
;;      delete-confirm-modal - delete drink
;;
;; should I pass the client around?
;; or is it better to just pass some functions down?
;;  - probably this
;; or should components call it directly? - would require having state in another ns
