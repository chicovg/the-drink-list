(ns the-beer-list.subs
  (:require
   [re-frame.core :as rf]))

(rf/reg-sub
 ::user
 (fn [db _](:user db)))

(rf/reg-sub
 ::is-logged-in
 (fn [db _] (some? (:user db))))

(rf/reg-sub
 ::name
 (fn [db]
   (:name db)))

(rf/reg-sub
 ::active-panel
 (fn [db _]
   (:active-panel db)))

(defn beer-list-filter-fn
  [filter]
  (if (nil? filter)
    (constantly true)
    (fn [beer]
      (or (clojure.string/includes? (or (:name beer) "") filter)
          (clojure.string/includes? (or (:brewery beer) "") filter)))))

(rf/reg-sub
 ::beers
 (fn [db _]
   (let [list-filter (:beer-list-filter db)
         filter-fn (beer-list-filter-fn list-filter)]
     (filter filter-fn (vals (:beer-map db))))))

(rf/reg-sub
 ::beer-modal
 (fn [db _]
   (:beer-modal db)))

(rf/reg-sub
 ::delete-confirm-modal
 (fn [db _]
   (:delete-confirm-modal db)))
