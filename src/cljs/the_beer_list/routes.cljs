(ns the-beer-list.routes
  (:import goog.History)
  (:require
   [secretary.core :as secretary :refer-macros [defroute]]
   [goog.events :as gevents]
   [goog.history.EventType :as EventType]
   [re-frame.core :as re-frame]
   [the-beer-list.events :as events]))

(defn hook-browser-navigation! []
  (doto (History.)
    (gevents/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defn app-routes []
  (secretary/set-config! :prefix "#")
  ;; --------------------
  ;; define routes here
  (defroute home "/" []
    (re-frame/dispatch [::events/set-active-panel :home-panel]))

  (defroute add-beer "/beer/new" []
    (re-frame/dispatch [::events/set-active-panel :add-beer-panel {}]))

  (defroute edit-beer "/beer/edit/:id" [id]
    (re-frame/dispatch [::events/set-active-panel :edit-beer-panel {:id id}]))

  (defroute about "/about" []
    (re-frame/dispatch [::events/set-active-panel :about-panel]))

  (defroute "*" []
    (re-frame/dispatch [::events/set-active-panel :not-found-panel]))

  ;; --------------------

  (hook-browser-navigation!))

