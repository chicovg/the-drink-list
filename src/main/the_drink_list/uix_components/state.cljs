(ns the-drink-list.uix-components.state
  (:require
   [the-drink-list.types.drink :as drink]
   [the-drink-list.types.beer-flavors :as beer-flavors]
   [uix.core :refer [use-state]]))

(defn- use-main-app-state
  []
  (let [[loading? set-loading!] (use-state true)
        [page set-page!]        (use-state :main)]
    {:loading?            loading?
     :set-loading!        set-loading!
     :page                page
     :set-page!           set-page!}))

(defn- use-main-page-state
  []
  (let [[search-term set-search-term!] (use-state nil)
        [sort-state set-sort-state!]   (use-state {:field :created
                                                   :asc?  false})]
    {:search-term      search-term
     :set-search-term! set-search-term!
     :sort-state       sort-state
     :set-sort-state!  set-sort-state!}))

(defn- use-delete-modal-state
  []
  (let [[delete-modal-drink-id set-delete-modal-drink-id!] (use-state nil)
        [show-delete-modal? set-show-delete-modal!]        (use-state false)

        show-delete-modal! (fn [id]
                             (set-delete-modal-drink-id! id)
                             (set-show-delete-modal! true))
        hide-delete-modal! (fn []
                             (set-show-delete-modal! false)
                             (set-delete-modal-drink-id! nil))]
    {:delete-modal-drink-id delete-modal-drink-id
     :show-delete-modal?    show-delete-modal?
     :show-delete-modal!    show-delete-modal!
     :hide-delete-modal!    hide-delete-modal!}))

(defn- use-drink-modal-state
  []
  (let [[drink-modal-drink set-drink-modal-drink!] (use-state nil)
        [show-drink-modal? set-show-drink-modal!]  (use-state false)

        show-drink-modal!            (fn [drink]
                                       (set-drink-modal-drink! drink)
                                       (set-show-drink-modal! true))
        hide-drink-modal!            (fn []
                                       (set-show-drink-modal! false)
                                       (set-drink-modal-drink! nil))]
    {:drink-modal-drink      drink-modal-drink
     :show-drink-modal?      show-drink-modal?
     :show-drink-modal!      show-drink-modal!
     :hide-drink-modal!      hide-drink-modal!
     :set-drink-modal-drink! set-drink-modal-drink!}))

(defn- distinct-values
  [key coll]
  (distinct
   (map key coll)))

(defn- use-drinks-state
  []
  (let [[drinks-map set-drinks-map!] (use-state nil)
        drinks                       (->> drinks-map
                                          vals
                                          (filter drink/is-valid?)
                                          (map drink/set-overall)
                                          (map drink/trim-fields))
        makers                       (distinct-values :maker drinks)
        styles                       (distinct-values :style drinks)
        types                        (distinct-values :type drinks)
        notes                        (->> drinks
                                          (map :notes)
                                          flatten
                                          (filter identity)
                                          distinct)
        notes-options                (-> beer-flavors/flavors
                                       (concat notes)
                                       distinct
                                       sort
                                       vec)]
    {:drinks          drinks
     :makers          makers
     :styles          styles
     :types           types
     :notes-options   notes-options

     :add-drink!      (fn [drink]
                        (set-drinks-map! (assoc drinks-map (:id drink) drink)))
     :remove-drink!   (fn [id]
                        (set-drinks-map! (dissoc drinks-map id)))
     :set-drinks-map! set-drinks-map!}))

(defn use-app-state
  ([] (use-app-state {}))
  ([overrides]
   (let [base-state (merge (use-main-app-state)
                           (use-main-page-state)
                           (use-delete-modal-state)
                           (use-drink-modal-state)
                           (use-drinks-state))]
     (merge base-state overrides))))
