(ns the-beer-list.components.drink-list
  (:require
   [the-beer-list.components.drink :as drink]
   [the-beer-list.types.drink :as drink-type]
   [clojure.string :as str]))

(defn- is-search-match
  [search-term {:keys [name maker type style]}]
  (let [term (str/lower-case search-term)]
    (or (empty? term)
        (or (str/includes? (str/lower-case name) term)
            (str/includes? (str/lower-case maker) term)
            (str/includes? (str/lower-case type) term)
            (str/includes? (str/lower-case style) term)))))

(defn- sort-fn
  [{:keys [asc? field]}]
  (cond->> #(sort-by field %)
    (not asc?)
    (comp reverse)))

(defn drink-list
  [{drinks             :drinks
    search-term        :search-term
    sort-state         :sort-state
    show-drink-modal!  :show-drink-modal!
    show-delete-modal! :show-delete-modal!}]
  (prn sort-state)
  (let [sort-drinks     (sort-fn sort-state)
        filtered-drinks (->> drinks
                             (filter drink-type/is-valid?)
                             (filter (partial is-search-match search-term))
                             sort-drinks)]
    [:div
     (cond
       (empty? drinks)
       [:div.notification.is-primary.is-light
        "No drinks logged yet."]

       (empty? filtered-drinks)
       [:div.notification.is-primary.is-light
        "No drinks match that search."]

       :else
       (for [drnk filtered-drinks]
         ^{:key (:id drnk)}
         [drink/card
          {:drink              drnk
           :show-drink-modal!  show-drink-modal!
           :show-delete-modal! show-delete-modal!}]))]))
