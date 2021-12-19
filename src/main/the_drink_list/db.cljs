(ns the-drink-list.db
  (:require
    [reagent.core :as r]))

(defonce app-db (r/atom {:delete-modal {:drink-id nil
                                        :shown?   false}
                         :drink-modal  {:drink  nil
                                        :shown? false}
                         :search-term  nil
                         :sort-state   {:field :created
                                        :asc?  false}}))

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

(defn search-term
  []
  (r/track #(:search-term @app-db)))

(defn set-search-term!
  [search-term]
  (swap! app-db assoc :search-term search-term))

(defn drink-modal-drink
  []
  (r/track #(get-in @app-db [:drink-modal :drink])))

(defn show-drink-modal?
  []
  (r/track #(get-in @app-db [:drink-modal :shown?])))

(defn set-drink-modal-drink!
  [drink]
  (swap! app-db assoc-in [:drink-modal :drink] drink))

(defn hide-drink-modal!
  []
  (swap! app-db assoc :drink-modal {:drink  nil
                                    :shown? false}))

(defn show-drink-modal!
  [drink]
  (swap! app-db assoc :drink-modal {:drink  drink
                                    :shown? true}))

(defn sort-state
  []
  (r/track #(:sort-state @app-db)))

(defn set-sort-state!
  [sort-state]
  (swap! app-db assoc :sort-state sort-state))
