(ns the-drink-list.uix-components.drink-list
  (:require
   [clojure.string :as str]
   [the-drink-list.uix-components.context :as context]
   [the-drink-list.uix-components.drink :as drink]
   [uix.core :refer [$ defui use-context]]))

(defn- is-search-match
  [search-term {:keys [name maker type style]}]
  (let [term (some-> search-term str/lower-case)]
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

(defui drink-list
  []
  (let [{:keys [drinks
                search-term
                sort-state]}   (use-context context/app)
        sort-drinks            (sort-fn sort-state)
        filtered-sorted-drinks (->> drinks
                                    (filter (partial is-search-match search-term))
                                    sort-drinks)]
    ($ :div
       (cond (empty? drinks)
             ($ :div.notification.is-primary.is-light
                "No drinks logged yet.")

             (empty? filtered-sorted-drinks)
             ($ :div.notification.is-primary.is-light
                "No drinks match that search.")

             :else
             (for [d filtered-sorted-drinks]
               ($ drink/card {:key   (:id d)
                              :drink d}))))))
