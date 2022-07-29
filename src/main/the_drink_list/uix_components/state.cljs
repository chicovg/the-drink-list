(ns the-drink-list.uix-components.state
  (:require
   [the-drink-list.types.drink :as drink]
   [the-drink-list.types.beer-flavors :as beer-flavors]
   [uix.core :refer [use-state]]))

(defn- use-main-app-state
  []
  (let [[loading? set-loading!] (use-state true)
        [page set-page!]        (use-state :main)

        ;; TODO might not need these!
        [user set-user!]        (use-state nil)
        handle-sign-in!         (fn [user]
                                  (set-user! user)
                                  (set-page! :main))
        handle-sign-out!         (fn []
                                  (set-user! nil)
                                  (set-page! :login))
        handle-auth-change!     (fn [user]
                                  (js/console.log "AUTH CHANGE")
                                  (if (:uid user)
                                    (handle-sign-in! user)
                                    (handle-sign-out!)))]
    {:loading?            loading?
     :set-loading!        set-loading!
     :page                page
     :set-page!           set-page!
     :user                user
     :handle-auth-change! handle-auth-change!
     :handle-sign-in!     handle-sign-in!}))

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
                                       (set-drink-modal-drink! nil))
        set-drink-modal-drink-value! (fn [key value]
                                       (set-drink-modal-drink!
                                        (assoc drink-modal-drink key value)))]
    {:drink-modal-drink            drink-modal-drink
     :show-drink-modal?            show-drink-modal?
     :show-drink-modal!            show-drink-modal!
     :hide-drink-modal!            hide-drink-modal!
     :set-drink-modal-drink-value! set-drink-modal-drink-value!}))

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
                                          distinct)
        notes-options                (-> beer-flavors/flavors
                                         (concat notes)
                                         sort
                                         vec)]
    (prn drinks-map)
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

(defn- use-favorites-panel-state
  [drinks]
  (let [[favorites-panel set-favorites-panel!]           (use-state :style)
        [favorites-sort-state set-favorites-sort-state!] (use-state {:field :average
                                                                         :asc?  false})

        favorites-data            (grouped-drink-totals drinks favorites-panel favorites-sort-state)
        set-favorites-sort-field! (fn [field]
                                    (let [current-field (:field favorites-sort-state)]
                                      (set-favorites-sort-state!
                                       (cond-> (assoc favorites-sort-state :field field)
                                         (= current-field field)
                                         (update :asc? not)))))]
    {:favorites-panel           favorites-panel
     :favorites-data            favorites-data
     :set-favorites-panel!      set-favorites-panel!
     :set-favorites-sort-field! set-favorites-sort-field!}))

(defn use-app-state
  ([] (use-app-state {}))
  ([overrides]
   (let [base-state (merge (use-main-app-state)
                           (use-main-page-state)
                           (use-delete-modal-state)
                           (use-drink-modal-state)
                           (use-drinks-state))]
     (merge base-state
            (use-favorites-panel-state (:drink base-state))
            overrides))))
