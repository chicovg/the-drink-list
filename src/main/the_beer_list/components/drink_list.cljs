(ns the-beer-list.components.drink-list
  (:require
   [reagent.core :as r]
   [the-beer-list.components.drink :as drink]))

(defn- on-sort-change [sort-state clicked-field]
  (if (= clicked-field (:field @sort-state))
    (swap! sort-state update :asc? not)
    (reset! sort-state {:field clicked-field
                        :asc?  false})))

(defn- options-nav
  [sort-state]
  [:nav.level
   [:div.level-left
    [:div.level-item
     [:p.control.has-icons-left
      [:input.input {:placeholder "Search"}]
      [:span.icon.is-left
       [:i.fas.fa-search {:aria-hidden "true"}]]]]
    [:div.level-item
     [:div.field.has-addons
      [:p.control
       [:button.button
        {:class (when (= :date (:field @sort-state))
                  "is-active")
         :on-click #(on-sort-change sort-state :date)}
        "Sort by Date"]]
      [:p.control
       [:button.button
        {:class (when (= :rating (:field @sort-state))
                  "is-active")
         :on-click #(on-sort-change sort-state :rating)}
        "Sort by Rating"]]]]
    [:div.level-item
     [:p.control
      [:button.button.is-primary
       [:span.icon
        [:i.fas.fa-plus]]
       [:span
        "Add Drink"]]]]]])

;; TODO
;; I need date on the drink data
;; I need to populate the overall rating - should I do this earlier? i.e. when getting the data

(defn drink-list
  [{:keys [drinks]}]
  (r/with-let [sort-state (r/atom {:field :date
                                   :asc?  false})]
    (fn []
      [:div.container
       [options-nav sort-state]
       [:div.p-2]
       [:div.drink-list.is-max-desktop
        (for [drnk drinks]
          ^{:key (:id drnk)}
          [drink/card drnk])]])))
