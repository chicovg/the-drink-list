(ns the-drink-list.db
  (:require
   [reagent.core :as r]
   [the-drink-list.api.firebase :as firebase]
   [the-drink-list.types.beer-flavors :as beer-flavors]
   [the-drink-list.types.drink :as drink]))

(def default-state
  {:delete-modal         {:drink-id nil
                          :shown?   false}
   :drinks               nil
   :drink-modal          {:drink  nil
                          :shown? false}
   :error                nil
   :favorites-panel      :style
   :favorites-sort-state {:field :average
                          :asc?  false}
   :loading?             false
   :page                 :login
   :search-term          nil
   :sort-state           {:field :created
                          :asc?  false}
   :user                 nil})

(defonce app-db (r/atom default-state))

(defn reset-app-db!
  ([] (reset-app-db! default-state))
  ([state] (reset! app-db state)))

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
  (r/track #(->> @app-db
                 :drinks
                 vals
                 (filter drink/is-valid?)
                 (map drink/set-overall)
                 (map drink/trim-fields))))

(defn- distinct-values
  [key coll]
  (distinct
   (map key coll)))

(defn makers
  []
  (r/track distinct-values :maker @(drinks)))

(defn styles
  []
  (r/track distinct-values :style @(drinks)))

(defn types
  []
  (r/track distinct-values :type @(drinks)))

(defn set-drinks!
  [drinks]
  (swap! app-db assoc :drinks drinks))

(defn- add-drink
  [drink]
  (swap! app-db update :drinks assoc (:id drink) drink))

(defn- remove-drink
  [id]
  (swap! app-db update :drinks dissoc id))

(declare set-loading!)

(defn load-drinks!
  [user]
  (firebase/get-drinks {:user         user
                        :set-loading! set-loading!
                        :on-success   set-drinks!
                        :on-error     #(js/console.error %)}))

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

;; favorites
(defn- merge-drink-ratings
  [drinks]
  (reduce (fn [{:keys [count total]} {:keys [overall]}]
            {:count (inc count)
             :total (+ total overall)})
          {:count 0
           :total 0}
          drinks))

(defn- maybe-reverse
  [reverse? coll]
  (if reverse?
    (reverse coll)
    coll))

(defn- grouped-drink-totals
  [drinks group-by-fn {:keys [field asc?]}]
  (->> drinks
       (group-by group-by-fn)
       (mapv (fn [[maker drinks]]
               [maker (merge-drink-ratings drinks)]))
       (mapv (fn [[maker {:keys [count total] :as totals}]]
               (if (zero? count)
                 [maker (assoc totals :average 0)]
                 [maker (assoc totals :average (drink/round (/ total count)))])))
       (sort-by (fn [[_ totals]]
                  (get totals field)))
       (maybe-reverse (not asc?))
       (mapv (fn [[maker {:keys [count average]}]]
               [maker count average]))
       (take 10)))

(defn favorites-panel
  []
  (r/track :favorites-panel @app-db))

(defn set-favorites-panel!
  [panel]
  (swap! app-db (fn [db]
                  (-> db
                      (assoc :favorites-panel panel)
                      (assoc :favorites-sort-state {:field :average
                                                    :asc?  false})))))

(defn favorites-sort-state
  []
  (r/track :favorites-sort-state @app-db))

(defn favorites-data
  []
  (r/track grouped-drink-totals @(drinks) @(favorites-panel) @(favorites-sort-state)))

(defn set-favorites-sort-field!
  [field]
  (swap! app-db (fn [db]
                  (let [current-field (get-in db [:favorites-sort-state :field])
                        db'           (assoc-in db [:favorites-sort-state :field] field)]
                    (if (= current-field field)
                      (update-in db' [:favorites-sort-state :asc?] not)
                      db')))))

;; loading?
(defn loading?
  []
  (r/track :loading? @app-db))

(defn set-loading!
  [loading?]
  (swap! app-db assoc :loading? loading?))

;; page
(defn page
  []
  (r/track :page @app-db))

(defn set-page!
  [page]
  (swap! app-db assoc :page page))

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

(defn set-user!
  [user]
  (swap! app-db assoc :user user))

(defn set-user-and-load-drinks!
  [user]
  (set-user! user)
  (if (:uid user)
    (do
      (load-drinks! user)
      (set-page! :main))
    (do
      (set-drinks! nil)
      (set-page! :login))))

(defn sign-in
  []
  (firebase/sign-in set-user-and-load-drinks!))

(defn sign-out
  []
  (firebase/sign-out))
