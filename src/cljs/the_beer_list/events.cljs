(ns the-beer-list.events
  (:require
   [re-frame.core :as re-frame]
   [com.degel.re-frame-firebase :as firebase]
   [the-beer-list.db :as db]
   [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]))

(re-frame/reg-event-fx
 :sign-in
 (fn [_ _] {:firebase/google-sign-in {:sign-in-method :popup}}))

(re-frame/reg-event-fx
 :sign-out
 (fn [_ _] {:firebase/google-sign-out nil}))

(re-frame/reg-event-db
 :set-user
 (fn [db [_ user]]
   (assoc db :user user)))

(re-frame/reg-event-fx
 :firebase-error
 (fn [_ error] (.log js/console error)))

(re-frame/reg-event-db
 ::initialize-db
 (fn-traced [_ _]
            db/default-db))

(re-frame/reg-event-db
 ::set-active-panel
 (fn-traced [db [_ active-panel]]
            (assoc db :active-panel active-panel)))

(re-frame/reg-event-db
 ::show-add-beer-modal
 (fn-traced [db _]
            (assoc db :beer-modal {:beer {:name ""
                                          :brewery ""
                                          :type ""
                                          :rating 3
                                          :comment ""}
                                   :operation :add
                                   :show true})))

(re-frame/reg-event-db
 ::show-edit-beer-modal
 (fn-traced [db [_ beer]]
            (assoc db :beer-modal {:beer beer
                                   :operation :edit
                                   :show true})))

(re-frame/reg-event-db
 ::hide-beer-modal
 (fn-traced [db [_ _]]
            (assoc db :beer-modal {:beer nil
                                   :operation nil
                                   :show false})))

(defn dispatch-hide-modal
  [context]
  (assoc-in context [:effects :dispatch] [::hide-beer-modal]))

(def hide-modal-after-save
  (re-frame/->interceptor
   :id :hide-modal-after-save
   :after dispatch-hide-modal))

(defn new-id
  [beer-map]
  (->> (keys beer-map)
       (reduce max 0)
       inc))

(re-frame/reg-event-db
 ::save-beer
 [hide-modal-after-save]
 (fn-traced [db [_ beer]]
            (let [id (or (:id beer)
                         (new-id (:beer-map db)))]
              (assoc-in db [:beer-map id] (assoc beer :id id)))))

(re-frame/reg-event-db
 ::show-delete-confirm-modal
 (fn-traced [db [_ id]]
            (assoc db :delete-confirm-modal {:id id
                                             :show true})))
(re-frame/reg-event-db
 ::hide-delete-confirm-modal
 (fn-traced [db _]
            (assoc db :delete-confirm-modal {:id nil
                                             :show false})))

(defn dispatch-hide-delete-confirm-modal
  [context]
  (assoc-in context [:effects :dispatch] [::hide-delete-confirm-modal]))

(def hide-delete-confirm-modal-after-delete
  (re-frame/->interceptor
   :id :hide-delete-confirm-modal-after-delete
   :after dispatch-hide-delete-confirm-modal))

(re-frame/reg-event-db
 ::delete-beer
 [hide-delete-confirm-modal-after-delete]
 (fn-traced [db [_ id]]
            (let [beer-map (:beer-map db)]
              (assoc db :beer-map (dissoc beer-map id)))))

(re-frame/reg-event-db
 ::set-beer-list-filter
 (fn-traced [db [_ filter]]
            (assoc db :beer-list-filter filter)))
