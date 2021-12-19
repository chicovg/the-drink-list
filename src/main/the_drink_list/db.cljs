(ns the-drink-list.db
  (:require
    [reagent.core :as r]
    [the-drink-list.api.firebase :as firebase]
    [the-drink-list.types.beer-flavors :as beer-flavors]))

(defonce app-db (r/atom {:delete-modal   {:drink-id nil
                                          :shown?   false}
                         :drinks         nil
                         :drinks-loaded? false
                         :drink-modal    {:drink  nil
                                          :shown? false}
                         :error          nil
                         :search-term    nil
                         :sort-state     {:field :created
                                          :asc?  false}
                         :user           nil}))

;; delete-modal
(defn delete-modal-drink-id
  []
  (r/track #(get-in @app-db [:delete-modal :drink-id])))

(defn show-delete-modal?
  []
  (r/track #(get-in @app-db [:delete-modal :shown?])))

(defn show-delete-modal!
  [id]
  (swap! app-db assoc :delete-modal {:drink-id id
                                     :shown?   true}))

(defn hide-delete-modal!
  []
  (swap! app-db assoc :delete-modal {:drink-id nil
                                     :shown?   false}))

;; drinks
(defn drinks
  []
  (r/track vals (:drinks @app-db)))

(defn set-drinks
  [drinks]
  (swap! app-db assoc :drinks drinks))

(defn- add-drink
  [drink]
  (swap! app-db update :drinks assoc (:id drink) drink))

(defn- remove-drink
  [id]
  (swap! app-db update :drinks dissoc id))

(defn load-drinks!
  [uid]
  (when (and uid (not (:drinks-loaded? @app-db)))
    (prn "loading")
    (firebase/get-drinks uid set-drinks)
    (swap! app-db assoc :drinks-loaded? true)))

(declare uid)

(defn delete-drink!
  [id]
  (when-let [uid @(uid)]
    (let [on-success (fn []
                       (remove-drink id)
                       (hide-delete-modal!))
          on-error   (fn [err] (prn err))]
      (firebase/delete-drink! uid id remove-drink on-success on-error))))

(defn- notes-options
  []
  (let [drinks          @(drinks)]
    (-> beer-flavors/flavors
         seq
         (concat (mapcat :notes drinks))
         distinct)))

(defn drink-notes-options
  []
  (r/track notes-options))

(declare hide-drink-modal!)

(defn save-drink!
  [drink]
  (when-let [uid (get-in @app-db [:user :uid])]
    (let [on-success (fn []
                       (add-drink drink)
                       (hide-drink-modal!))
          on-error   (fn [err] (prn err))]
      (firebase/save-drink! uid drink add-drink on-success on-error))))

;; drink-modal
(defn drink-modal-drink
  []
  (r/track #(get-in @app-db [:drink-modal :drink])))

(defn show-drink-modal?
  []
  (r/track #(get-in @app-db [:drink-modal :shown?])))

(defn set-drink-modal-drink-value!
  [key value]
  (swap! app-db assoc-in [:drink-modal :drink key] value))

(defn hide-drink-modal!
  []
  (swap! app-db assoc :drink-modal {:drink  nil
                                    :shown? false}))

(defn show-drink-modal!
  [drink]
  (swap! app-db assoc :drink-modal {:drink  drink
                                    :shown? true}))

;; search-term
(defn search-term
  []
  (r/track #(:search-term @app-db)))

(defn set-search-term!
  [search-term]
  (swap! app-db assoc :search-term search-term))

(defn sort-state
  []
  (r/track #(:sort-state @app-db)))

(defn set-sort-state!
  [sort-state]
  (swap! app-db assoc :sort-state sort-state))

;; user
(defn uid
  []
  (r/track get-in @app-db [:user :uid]))

(defn set-user
  [user]
  (swap! app-db assoc :user user))

(defn sign-in
  []
  (firebase/sign-in set-user))

(defn sign-out
  []
  (firebase/sign-out set-user set-drinks))
