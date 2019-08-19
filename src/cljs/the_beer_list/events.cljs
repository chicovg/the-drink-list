(ns the-beer-list.events
  (:require
   [clojure.string :as s]
   [re-frame.core :as rf]
   [com.degel.re-frame-firebase :as firebase]
   [the-beer-list.db :as db]
   [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]))

;; state functions

(defn next-state
  [state-machine current-state transition]
  (let [next-state (get-in state-machine [current-state transition])]
    (or next-state
        current-state)))

;; general events

(rf/reg-event-db
 ::initialize-db
 (fn-traced [db _] db/default-db))

(rf/reg-event-db
 ::set-active-panel
 (fn-traced [db [_ active-panel]]
            (assoc db :active-panel active-panel)))

;; interceptors

(declare update-loading-modal-state)
(declare hide-loading-modal)

(def hide-loading-modal
  (rf/->interceptor
   :id :hide-loading-modal
   :after (fn-traced [context]
                     (let [{db :db} (:effects context)]
                       (assoc-in context
                                 [:effects :db]
                                 (update-loading-modal-state db :hide))))))

;; Beer data events

(rf/reg-event-db
 ::clear-beer-map
 (fn-traced [db _]
            (assoc db :beer-map {})))

(defn keys-to-symbols
  [data]
  (into {}
        (for [[k v] data]
          [(keyword k) v])))

(defn convert-data
  [{docs :docs}]
  (into {}
        (for [{id :id data :data} docs]
          (let [data-map (keys-to-symbols data)
                data-with-ids (assoc data-map :id id)]
            [id data-with-ids]))))

(rf/reg-event-db
 ::set-beer-map
 (fn-traced [db [_ data]]
            (let [beer-map (:beer-map db)
                  updates (convert-data data)]
              (assoc db :beer-map (merge beer-map updates)))))

(rf/reg-event-db
 ::set-beer-list-filter
 (fn-traced [db [_ filter]]
            (assoc db :beer-list-filter filter)))

;; firebase/ firestore events

(declare update-log-in-state)

(rf/reg-event-fx
 ::sign-in
 (fn-traced [{db :db} _]
            {:db (update-log-in-state db :log-in)
             :dispatch [::sign-in-with-firebase]}))

(rf/reg-event-fx
 ::sign-in-with-firebase
 (fn [_ _] {:firebase/google-sign-in {:sign-in-method :redirect}}))

(rf/reg-event-fx
 ::sign-out
 (fn-traced [{db :db} _]
            {:db (update-log-in-state db :log-out)
             :dispatch [::sign-out-with-firebase]}))

(rf/reg-event-fx
 ::sign-out-with-firebase
 (fn [_ _] {:firebase/sign-out {}}))

(rf/reg-event-fx
 ::set-user
 (fn-traced [{db :db} [_ user]]
            (if (some? user)
              {:db (-> db
                       (assoc :user user)
                       (update-log-in-state :user-received))
               :dispatch [::fetch-from-firestore (:uid user)]}
              {:db (-> db
                       (assoc :user nil)
                       (update-log-in-state db :no-user-received))
               :dispatch [::clear-beer-map]})))

(rf/reg-event-db
 ::firestore-failure
 (fn-traced [db [_ failure]]
            (assoc db :firestore-failure failure)))

(defn on-fetch-failure
  [error]
  (rf/dispatch [::firestore-failure error]))

(defn on-fetch-success
  [data]
  (do
    (rf/dispatch [::set-beer-map data])
    (rf/dispatch [::firestore-failure nil])))

(rf/reg-event-fx
 ::fetch-from-firestore
 (fn-traced [{db :db} [_ uid]]
            {:firestore/on-snapshot {:path-collection [:beers]
                                     :where [[:uid :== uid]]
                                     :on-next on-fetch-success
                                     :on-failure on-fetch-failure}}))

(defn on-write-failure
  []
  (rf/dispatch [::update-beer-modal-state :firestore-failure]))

(defn on-write-success
  []
  (do
    (rf/dispatch [::update-beer-modal-state :firestore-success])
    (rf/dispatch [::firestore-failure nil])))

(rf/reg-event-fx
 ::write-beer-to-firestore
 (fn-traced [{db :db} [_ beer]]
            (let [{uid :uid} (:user db)
                  beer-with-uid (assoc beer :uid uid)
                  id (:id beer-with-uid)]
              (if id
                {:firestore/set {:path [:beers id]
                                 :data beer-with-uid
                                 :on-success on-write-success
                                 :on-failure on-write-failure}}
                {:firestore/add {:path [:beers]
                                 :data beer-with-uid
                                 :on-success on-write-success
                                 :on-failure on-write-failure}}))))

(defn on-delete-failure
  [error]
  (do
    (rf/dispatch [::firestore-failure error])
    (rf/dispatch [::update-delete-confirm-state :firestore-failure])))

(defn on-delete-success
  [id]
  (do
    (rf/dispatch [::delete-beer-locally id])
    (rf/dispatch [::firestore-failure nil])))

(rf/reg-event-fx
 ::delete-beer-from-firestore
 (fn-traced [{db :db} [_ id]]
            {:firestore/delete {:path [:beers id]
                                :on-success (partial on-delete-success id)
                                :on-failure on-delete-failure}}))

(declare update-delete-confirm-state)

(rf/reg-event-fx
 ::delete-beer
 (fn-traced [{db :db} [_ id]]
            {:db (update-delete-confirm-state db :try-delete)
             :dispatch [::delete-beer-from-firestore id]}))

(rf/reg-event-fx
 ::delete-beer-locally
 (fn-traced [{db :db} [_ id]]
            (let [beer-map (:beer-map db)]
              {:db (assoc db :beer-map (dissoc beer-map id))
               :dispatch [::update-delete-confirm-state :firestore-success]})))

;; beer modal events

(defn update-beer-modal-state
  [db event]
  (update db :beer-modal-state (partial next-state db/beer-modal-states) event))

(rf/reg-event-db
 ::update-beer-modal-state
 (fn-traced [db [_ transition]]
            (update-beer-modal-state db transition)))

(rf/reg-event-fx
 ::try-save-beer
 (fn-traced [{db :db} [_ {:keys [name brewery type] :as beer}]]
            (cond
              (s/blank? name) {:db (update-beer-modal-state db :save-no-name)}
              (s/blank? brewery) {:db (update-beer-modal-state db :save-no-brewery)}
              (s/blank? type) {:db (update-beer-modal-state db :save-no-type)}
              :else {:db (update-beer-modal-state db :try-save)
                     :dispatch [::write-beer-to-firestore beer]})))

(rf/reg-event-fx
 ::show-add-beer-modal
 (fn-traced [{db :db} _]
            {:db (assoc db :beer-modal {:beer {:name nil
                                               :brewery nil
                                               :type nil
                                               :rating 3
                                               :comment nil}
                                        :operation :add})
             :dispatch [::update-beer-modal-state :show]}))

(rf/reg-event-fx
 ::show-edit-beer-modal
 (fn-traced [{db :db} [_ beer]]
            {:db (assoc db :beer-modal {:beer beer
                                        :operation :edit})
             :dispatch [::update-beer-modal-state :show]}))

(rf/reg-event-fx
 ::clear-and-hide-beer-modal
 (fn-traced [{db :db} _]
            {:db (assoc db :beer-modal db/beer-modal-default)
             :dispatch [::update-beer-modal-state :hide]}))

;; beer modal beer

(rf/reg-event-fx
 ::set-beer-modal-value
 (fn-traced [{db :db} [_ field value]]
            {:db (assoc-in db [:beer-modal :beer field] value)
             :dispatch [::update-beer-modal-state :field-changed]}))

;; delete confirm modal events

(defn update-delete-confirm-state
  [db event]
  (update db :delete-confirm-state (partial next-state db/delete-confirm-states) event))

(rf/reg-event-db
 ::update-delete-confirm-state
 (fn-traced [db [_ transition]]
            (update-delete-confirm-state db transition)))

(rf/reg-event-fx
 ::show-delete-confirm-modal
 (fn-traced [{db :db} [_ id]]
            {:db (assoc db :delete-confirm-id id)
             :dispatch [::update-delete-confirm-state :show]}))

;; loading modal events

(defn update-loading-modal-state
  [db event]
  (update db :loading-modal-state (partial next-state db/loading-modal-states) event))

(rf/reg-event-db
 ::update-loading-modal-state
 (fn-traced [db [_ transition]]
            (update-loading-modal-state db transition)))

;; log in events

(defn update-log-in-state
  [db event]
  (update db :log-in-state (partial next-state db/log-in-states) event))

(rf/reg-event-db
 ::update-log-in-state
 (fn-traced [db [_ transition]]
            (update-log-in-state db transition)))

