(ns the-beer-list.components.options-nav
  (:require [the-beer-list.types.drink :as drink-type]))

(defn- search-input
  [set-search-term!]
  [:div.level-item
   [:p.control.has-icons-left
    [:input.input.is-small
     {:on-change   #(set-search-term! (-> % .-target .-value))
      :placeholder "Search"}]
    [:span.icon.is-left
     [:i.fas.fa-search {:aria-hidden "true"}]]]])

(defn- new-sort-state [sort-state clicked-field]
  (if (= clicked-field (:field sort-state))
    (update sort-state :asc? not)
    {:asc?  false
     :field clicked-field}))

(defn- sort-buttons
  [field sort-state set-sort-state!]
  [:div.level-item
   [:div.field.has-addons
    [:p.control
     [:button.button.is-small
      {:class    (when (= :date field) "is-active")
       :on-click #(-> sort-state
                      (new-sort-state :date)
                      set-sort-state!)}
      "Sort by Date"]]
    [:p.control
     [:button.button.is-small
      {:class    (when (= :rating field) "is-active")
       :on-click #(-> sort-state
                      (new-sort-state :rating)
                      set-sort-state!)}
      "Sort by Rating"]]]])

(defn- new-drink-button
  [show-drink-modal!]
  [:div.level-item
   [:p.control
    [:button.button.is-primary.is-small
     {:on-click #(show-drink-modal! drink-type/default-values)}
     [:span.icon
      [:i.fas.fa-plus]]
     [:span "New"]]]])

(defn options-nav
  [{set-search-term!    :set-search-term!
    set-sort-state!     :set-sort-state!
    show-drink-modal!   :show-drink-modal!
    {:keys [asc? field]
     :as   sort-state}  :sort-state}]
  [:nav.level.mr-2.ml-2
   [:div.level-left
    [search-input set-search-term!]]
   [:div.level-right
    ;; TODO note sure how I want to present this...
    #_[sort-buttons field sort-state set-sort-state!]
    [new-drink-button show-drink-modal!]]])
