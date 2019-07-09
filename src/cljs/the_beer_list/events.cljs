(ns the-beer-list.events
  (:require
   [re-frame.core :as re-frame]
   [com.degel.re-frame-firebase :as firebase]
   [the-beer-list.db :as db]
   [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]))

(re-frame/reg-event-fx
 :sign-in
 (fn [_ _ ]{:firebase/google-sign-in {:sign-in-method :popup}}))

(re-frame/reg-event-fx
 :sign-out
 (fn [_ _]{:firebase/google-sign-out nil}))

(re-frame/reg-event-db
 :set-user
 (fn [db [_ user]]
   (assoc db :user user)))

(re-frame/reg-event-fx
 :firebase-error
 (fn [_ error](.log js/console error)))

(re-frame/reg-event-db
 ::initialize-db
 (fn-traced [_ _]
            db/default-db))

(re-frame/reg-event-db
 ::set-active-panel
 (fn-traced [db [_ active-panel]]
            (assoc db :active-panel active-panel)))
