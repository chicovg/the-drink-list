(ns the-beer-list.components.drink-list
  (:require
   [the-beer-list.components.drink :as drink]
   [clojure.string :as str]))

(defn- is-search-match
  [search-term {:keys [name maker type style]}]
  (let [term (str/lower-case search-term)]
    (or (empty? term)
        (or (str/includes? (str/lower-case name) term)
            (str/includes? (str/lower-case maker) term)
            (str/includes? (str/lower-case type) term)
            (str/includes? (str/lower-case style) term)))))

;; TODO
;; I need date on the drink data
;; I need to populate the overall rating - should I do this earlier? i.e. when getting the data

(defn drink-list
  [{drinks               :drinks
    search-term          :search-term
    {:keys [asc? field]} :sort-state}]
  [:div.drink-list
   (for [drnk (filter
               (partial is-search-match search-term)
               drinks)]
     ^{:key (:id drnk)}
     [drink/card drnk])])
