(ns the-beer-list.events
  (:require
   [re-frame.core :as rf]
   [com.degel.re-frame-firebase :as firebase]
   [the-beer-list.db :as db]
   [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]))

(rf/reg-event-db
 ::initialize-db
 (fn-traced [_ _]
            db/default-db))

(rf/reg-event-db
 ::set-active-panel
 (fn-traced [db [_ active-panel]]
            (assoc db :active-panel active-panel)))

;; modal events

(rf/reg-event-db
 ::show-add-beer-modal
 (fn-traced [db _]
            (assoc db :beer-modal {:beer {:name nil
                                          :brewery nil
                                          :type nil
                                          :rating 3
                                          :comment nil}
                                   :operation :add
                                   :show true})))

(rf/reg-event-db
 ::show-edit-beer-modal
 (fn-traced [db [_ beer]]
            (assoc db :beer-modal {:beer beer
                                   :operation :edit
                                   :show true})))

(rf/reg-event-db
 ::hide-beer-modal
 (fn-traced [db [_ _]]
            (assoc db :beer-modal {:beer nil
                                   :operation nil
                                   :show false})))

(defn dispatch-hide-modal
  [context]
  (assoc-in context [:effects :dispatch] [::hide-beer-modal]))

(rf/reg-event-db
 ::show-delete-confirm-modal
 (fn-traced [db [_ id]]
            (assoc db :delete-confirm-modal {:id id
                                             :show true})))
(rf/reg-event-db
 ::hide-delete-confirm-modal
 (fn-traced [db _]
            (assoc db :delete-confirm-modal {:id nil
                                             :show false})))

(defn dispatch-hide-delete-confirm-modal
  [context]
  (assoc-in context [:effects :dispatch] [::hide-delete-confirm-modal]))

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
            (js/console.log (convert-data data))
            (let [beer-map (:beer-map db)
                  updates (convert-data data)]
              (assoc db :beer-map (merge beer-map updates)))))

(def hide-modal-after-save
  (rf/->interceptor
   :id :hide-modal-after-save
   :after dispatch-hide-modal))

(defn new-id
  [beer-map]
  (->> (keys beer-map)
       (reduce max 0)
       inc))

(rf/reg-event-db
 ::save-beer
 [hide-modal-after-save]
 (fn-traced [db [_ beer]]
            (let [id (or (:id beer)
                         (new-id (:beer-map db)))]
              (assoc-in db [:beer-map id] (assoc beer :id id)))))

(def hide-delete-confirm-modal-after-delete
  (rf/->interceptor
   :id :hide-delete-confirm-modal-after-delete
   :after dispatch-hide-delete-confirm-modal))

(rf/reg-event-db
 ::delete-beer
 [hide-delete-confirm-modal-after-delete]
 (fn-traced [db [_ id]]
            (let [beer-map (:beer-map db)]
              (assoc db :beer-map (dissoc beer-map id)))))

(rf/reg-event-db
 ::set-beer-list-filter
 (fn-traced [db [_ filter]]
            (assoc db :beer-list-filter filter)))

;; firebase/ firestore events

(rf/reg-event-fx
 ::sign-in
 (fn [_ _] {:firebase/google-sign-in {:sign-in-method :popup}}))

(rf/reg-event-fx
 ::sign-out
 (fn [_ _] {:firebase/sign-out nil}))

(defn set-user-handler
  [{db :db} [_ user]]
  {:db (assoc db :user user)
   :dispatch (if (some? user)
               [::fetch-from-firestore (:uid user)]
               [::clear-beer-map])})

(rf/reg-event-db
 ::firebase-error
 (fn-traced [{db :db} [_ error]]
            (assoc-in db [:firebase :error] error)))

(rf/reg-event-fx
 ::set-user
 set-user-handler)

(defn fetch-from-firestore-handler
  [{db :db} [_ uid]]
  {:firestore/on-snapshot {:path-collection [:beers]
                           :where [[:uid :== uid]]
                           :on-next #(rf/dispatch [::set-beer-map %])
                           :on-failure #(rf/dispatch [:firebase-error %])}})

(rf/reg-event-fx
 ::fetch-from-firestore
 fetch-from-firestore-handler)

(defn write-beer-to-firestore-handler
  [{db :db} [_ beer]]
  (let [{uid :uid} (:user db)
        beer-with-uid (assoc beer :uid uid)
        id (:id beer-with-uid)]
    (if id
      {:firestore/set {:path [:beers id]
                       :data beer-with-uid
                       :on-success #(js/console.log %)
                       :on-failure #(rf/dispatch [::firebase-error %])}}
      {:firestore/add {:path [:beers]
                       :data beer-with-uid
                       :on-success #(js/console.log %)
                       :on-failure #(rf/dispatch [::firebase-error %])}})))

(rf/reg-event-fx
 ::write-beer-to-firestore
 [hide-modal-after-save]
 write-beer-to-firestore-handler)
