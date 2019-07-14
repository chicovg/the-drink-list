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

(re-frame/reg-sub
 ::beers
 (fn [db _]
   (vals (:beer-map db))))

(re-frame/reg-sub
 ::beer-modal
 (fn [db _]
   (:beer-modal db)))
