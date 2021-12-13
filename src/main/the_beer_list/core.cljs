(ns the-beer-list.core
  (:require [reagent.core :as r]
            [reagent.dom :as dom]
            [the-beer-list.api.firebase :as firebase]
            [the-beer-list.components.main-page :as main-page]))

(def dom-root (js/document.getElementById "app"))

(defonce app-db (r/atom {:drinks nil
                         :user   nil}))

(defn- set-user
  [user]
  (swap! app-db assoc :user user))

(defn- set-drinks
  [drinks]
  (swap! app-db assoc :drinks drinks))

(defn- add-drink
  [drink]
  (swap! app-db update :drinks assoc (:id drink) drink))

(defn- root
  []
  (let [uid (some-> @app-db :user :uid)
        _   (when (and uid (nil? (:drinks @app-db)))
              (firebase/get-drinks uid set-drinks))]
    (prn @app-db)
    [main-page/main-page {:drinks      (vals
                                        (:drinks @app-db))
                          :save-drink! (fn [drink on-success on-error]
                                         (firebase/save-drink! uid drink add-drink on-success on-error))
                          :sign-in     (fn []
                                         (firebase/sign-in set-user))
                          :sign-out    (fn []
                                         (firebase/sign-out set-user set-drinks))
                          :uid         uid}]))

(defn ^:dev/after-load start []
  (js/console.log "start")
  (firebase/listen-to-auth set-user)
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
