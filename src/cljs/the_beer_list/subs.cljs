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

(defn beer-list-filter-fn
  [filter]
  (if (nil? filter)
    (constantly true)
    (fn [beer]
      (or (clojure.string/includes? (:name beer) filter)
          (clojure.string/includes? (:brewery beer) filter)))))

(re-frame/reg-sub
 ::beers
 (fn [db _]
   (let [list-filter (:beer-list-filter db)
         filter-fn (beer-list-filter-fn list-filter)]
     (filter filter-fn (vals (:beer-map db))))))

(re-frame/reg-sub
 ::beer-modal
 (fn [db _]
   (:beer-modal db)))

(re-frame/reg-sub
 ::delete-confirm-modal
 (fn [db _]
   (:delete-confirm-modal db)))
