(ns the-beer-list.components.options-nav)

(defn- new-sort-state [sort-state clicked-field]
  (if (= clicked-field (:field sort-state))
    (update sort-state :asc? not)
    {:asc?  false
     :field clicked-field}))

(defn options-nav
  [{set-search-term     :set-search-term
    set-sort-state      :set-sort-state
    {:keys [asc? field]
     :as   sort-state}  :sort-state}]
  [:nav.level
   [:div.level-left
    [:div.level-item
     [:p.control.has-icons-left
      [:input.input.is-small
       {:on-change   #(set-search-term (-> % .-target .-value))
        :placeholder "Search"}]
      [:span.icon.is-left
       [:i.fas.fa-search {:aria-hidden "true"}]]]]
    [:div.level-item
     [:div.field.has-addons
      [:p.control
       [:button.button.is-small
        {:class    (when (= :date field) "is-active")
         :on-click #(-> sort-state
                        (new-sort-state :date)
                        set-sort-state)}
        "Sort by Date"]]
      [:p.control
       [:button.button.is-small
        {:class    (when (= :rating field) "is-active")
         :on-click #(-> sort-state
                        (new-sort-state :rating)
                        set-sort-state)}
        "Sort by Rating"]]]]
    [:div.level-item
     [:p.control
      [:button.button.is-primary.is-small
       [:span.icon
        [:i.fas.fa-plus]]
       [:span
        "Add Drink"]]]]]])
