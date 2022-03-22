(ns the-drink-list.components.favorites-page
  (:require
   [the-drink-list.db :as db]
   [the-drink-list.types.drink :as drink-type]
   [the-drink-list.components.common :as common]))

(defn- sort-icon
  [asc?]
  [:span.icon.is-small
   (if asc?
     [:i.fas.fa-caret-up]
     [:i.fas.fa-caret-down])])

(defn- favorites-table
  [first-header data {:keys [field asc?]}]
  (when (and first-header data)
    [:table.table
     [:thead
      [:tr
       [:th first-header]
       [:th.is-clickable
        {:on-click #(db/set-favorites-sort-field! :count)}
        "Logged"
        (when (= field :count)
          [sort-icon asc?])]
       [:th.is-clickable
        {:on-click #(db/set-favorites-sort-field! :average)}
        "Avg. Rating"
        (when (= field :average)
          [sort-icon asc?])]]]
     [:tbody
      (for [[item count average] data]
        ^{:key item}
        [:tr
         [:td item]
         [:td count]
         [:td
          (let [avg-rating (drink-type/round average)]
            [:span.overall
             {:class (common/rating-class avg-rating)}
             (drink-type/round avg-rating)])]])]]))

(defn favorites-page
  []
  (let [favorites-data       @(db/favorites-data)
        favorites-panel      @(db/favorites-panel)
        favorites-sort-state @(db/favorites-sort-state)]
    [:div.mt-4.has-background-white
     [:div.tabs
      [:ul
       [:li {:class (when (= :style favorites-panel) "is-active")}
        [:a {:on-click #(db/set-favorites-panel! :style)}
         "Favorite Styles"]]
       [:li {:class (when (= :maker favorites-panel) "is-active")}
        [:a {:on-click #(db/set-favorites-panel! :maker)}
         "Favorite Makers"]]]]
     [:div.pb-5.pl-4.pr-4
      [favorites-table
       (condp = favorites-panel
         :style "Style"
         :maker "Maker"
         nil)
       favorites-data
       favorites-sort-state]]]))
