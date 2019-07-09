(ns the-beer-list.core
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [breaking-point.core :as bp]
   [com.degel.re-frame-firebase :as fb]
   [the-beer-list.events :as events]
   [the-beer-list.routes :as routes]
   [the-beer-list.views :as views]
   [the-beer-list.config :as config]))

;; TODO make the api key an env variable
(defonce fb-app-info {:apiKey "AIzaSyDKNRNCtfbhp1YRyggoHi0e-ud24HSRnoU"
                      :authDomain "the-beer-list-245600.firebaseapp.com"
                      :databaseURL "https://the-beer-list-245600.firebaseio.com"
                      :projectId "the-beer-list-245600.appspot.com"
                      :storageBucket "the-beer-list-245600.appspot.com"})

(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (routes/app-routes)
  (re-frame/dispatch-sync [::events/initialize-db])
  (re-frame/dispatch-sync [::bp/set-breakpoints
                           {:breakpoints [:mobile
                                          768
                                          :tablet
                                          992
                                          :small-monitor
                                          1200
                                          :large-monitor]
                            :debounce-ms 166}])
  (fb/init :firebase-app-info fb-app-info
           :firestore-settings {:timestampsInSnapshots true}
           :get-user-sub [:user]
           :set-user-event [:set-user]
           :default-error-handler [:firebase-error])
  (dev-setup)
  (mount-root))
