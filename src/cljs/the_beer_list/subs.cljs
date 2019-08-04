(ns the-beer-list.subs
  (:require
   [re-frame.core :as rf]))

;; user

(rf/reg-sub
 ::user
 (fn [db _](:user db)))

(rf/reg-sub
 ::is-logged-in?
 (fn [db _] (some? (:user db))))

;; nav

(rf/reg-sub
 ::active-panel
 (fn [db _]
   (:active-panel db)))

;; beer data

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
     (->> (:beer-map db)
         vals
         (filter filter-fn)
         (sort-by :name)))))

;; beer modal state


(rf/reg-sub
 ::beer-modal-state
 (fn [db _]
   (:beer-modal-state db)))

(rf/reg-sub
 ::beer-modal-showing?
 (fn [db _] (rf/subscribe [::beer-modal-state]))
 (fn [state _] (not= state :ready)))

(rf/reg-sub
 ::save-failed?
 (fn [db _] (rf/subscribe [::beer-modal-state]))
 (fn [state _] (= state :save-failed)))

(rf/reg-sub
 ::beer-modal-field-error
 (fn [db _] (rf/subscribe [::beer-modal-state]))
 (fn [state [_ field]] (case field
                         :name (case state
                                 :name-required "Name required"
                                 nil)
                         :brewery (case state
                                    :brewery-required "Brewery required"
                                    nil)
                         :type (case state
                                 :type-required "Type required"
                                 nil)
                         nil)))

;; beer modal beer

(rf/reg-sub
 ::beer-modal
 (fn [db _] (:beer-modal db)))

(rf/reg-sub
 ::beer-modal-beer
 (fn [db _] (rf/subscribe [::beer-modal]))
 (fn [beer-modal _] (:beer beer-modal)))

(rf/reg-sub
 ::beer-modal-is-adding?
 (fn [db _] (rf/subscribe [::beer-modal]))
 (fn [beer-modal _] (= :add (:operation beer-modal))))

(rf/reg-sub
 ::beer-modal-field-value
 (fn [db _] (rf/subscribe [::beer-modal-beer]))
 (fn [beer [_ field]] (field beer)))


;; delete confirm modal

(rf/reg-sub
 ::delete-confirm-state
 (fn [db _]
   (:delete-confirm-state db)))

(rf/reg-sub
 ::delete-confirm-modal-showing?
 (fn [db _] (rf/subscribe [::delete-confirm-state]))
 (fn [state _] (not= state :ready)))

(rf/reg-sub
 ::delete-failed?
 (fn [db _] (rf/subscribe [::delete-confirm-state]))
 (fn [state _] (= state :delete-failed)))

(rf/reg-sub
 ::delete-confirm-id
 (fn [db _]
   (:delete-confirm-id db)))

;; loading modal

(rf/reg-sub
 ::loading-modal-state
 (fn [db _]
   (:loading-modal-state db)))

(rf/reg-sub
 ::loading-modal-showing?
 (fn [db _] (rf/subscribe [::loading-modal-state]))
 (fn [state _] (= state :showing)))

;; log in state

(rf/reg-sub
 ::log-in-state
 (fn [db _]
   (:log-in-state db)))

(rf/reg-sub
 ::log-in-failed?
 (fn [db _] (rf/subscribe [::log-in-state]))
 (fn [state _] (= state :log-in-failed)))
