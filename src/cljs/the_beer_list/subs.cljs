(ns the-beer-list.subs
  (:require
   [clojure.string :as s]
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
      (or (s/includes? (or (:name beer) "") filter)
          (s/includes? (or (:brewery beer) "") filter)))))

(rf/reg-sub
 ::beers
 (fn [db _]
   (let [list-filter (:beer-list-filter db)
         filter-fn (beer-list-filter-fn list-filter)
         {:keys [field order]} (:beer-list-sort db)
         sort-fn (if (= order :asc) < >)]
     (->> (:beer-map db)
         vals
         (filter filter-fn)
         (sort-by field sort-fn)))))

(rf/reg-sub
 ::beer
 (fn [db [_ id]]
   (get-in db [:beer-map id])))

(rf/reg-sub
 ::beer-list-sort
 (fn [db _]
   (:beer-list-sort db)))

;; beer form state

(rf/reg-sub
 ::beer-form-state
 (fn [db _]
   (:beer-form-state db)))

(rf/reg-sub
 ::is-saving?
 (fn [_ _] (rf/subscribe [::beer-form-state]))
 (fn [state _] (= state :saving)))

(rf/reg-sub
 ::save-failed?
 (fn [_ _] (rf/subscribe [::beer-form-state]))
 (fn [state _] (= state :save-failed)))

(rf/reg-sub
 ::beer-form-field-error
 :<- [::beer-form-state]
 (fn [state [_ field]]
   (when (and
          (#{:name :brewery :type} field)
          (s/blank? (get state field)))
     "required!")))

;; beer form

(rf/reg-sub
 ::beer-form
 (fn [db _] (:beer-form db)))

(rf/reg-sub
 ::beer-form-beer
 (fn [_ _] (rf/subscribe [::beer-form]))
 (fn [beer-form _] (:beer beer-form)))

(rf/reg-sub
 ::beer-form-field-value
 (fn [_ _] (rf/subscribe [::beer-form-beer]))
 (fn [beer [_ field]] (field beer)))

;; delete confirm modal

(rf/reg-sub
 ::delete-confirm-state
 (fn [db _]
   (:delete-confirm-state db)))

(rf/reg-sub
 ::delete-confirm-modal-showing?
 (fn [_ _] (rf/subscribe [::delete-confirm-state]))
 (fn [state _] (not= state :ready)))

(rf/reg-sub
 ::delete-failed?
 (fn [_ _] (rf/subscribe [::delete-confirm-state]))
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
 (fn [_ _] (rf/subscribe [::loading-modal-state]))
 (fn [state _] (= state :showing)))

;; log in state

(rf/reg-sub
 ::log-in-state
 (fn [db _]
   (:log-in-state db)))

(rf/reg-sub
 ::log-in-failed?
 (fn [_ _] (rf/subscribe [::log-in-state]))
 (fn [state _] (= state :log-in-failed)))

;; sort modal state

(rf/reg-sub
 ::sort-modal-state
 (fn [db _]
   (:sort-modal-state db)))

(rf/reg-sub
 ::sort-modal-showing?
 (fn [_ _] (rf/subscribe [::sort-modal-state]))
 (fn [state _] (= state :showing)))

