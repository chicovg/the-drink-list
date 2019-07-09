(ns the-beer-list.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 :user
 (fn [db _](:user db)))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
 ::active-panel
 (fn [db _]
   (:active-panel db)))
