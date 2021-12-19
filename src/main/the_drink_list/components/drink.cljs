(ns the-drink-list.components.drink
  (:require
   [reagent.core :as r]
   [the-drink-list.components.common :as common]
   [the-drink-list.components.logged-at :as logged-at]))

(defn- overall-rating-badge
  [overall]
  [:span.is-small.is-pulled-right.is-rounded.overall
   {:class (common/rating-class overall)}
   overall])

(defn- summary
  [{:keys [created name maker style]}]
  [:<>
   [:p.is-clipped.mr-2 [:strong name]]
   [:p.mr-2.is-size-7 maker]
   [:p.is-size-7.is-italic style]
   [logged-at/logged-at created]])

(defn- actions
  [{:keys [on-details-toggle
           on-edit
           on-delete
           show-details?]}]
  [:nav.level.is-mobile
   [:div.level-left
    [:a.level-item {:on-click on-details-toggle}
     [:span.icon.is-small
      [:i.fas {:aria-hidden "true"
               :class       (if show-details?
                              "fa-chevron-up"
                              "fa-chevron-down")}]]]
    [:a.level-item {:on-click on-edit}
     [:span.icon.is-small
      [:i.fas.fa-edit {:aria-hidden "true"}]]]
    [:a.level-item {:on-click on-delete}
     [:span.icon.is-small
      [:i.fas.fa-trash {:aria-hidden "true"}]]]]])

(defn- rating-row
  [name rating bold?]
  [:div.is-flex.is-size-7
   {:class (when bold? "has-text-weight-bold")}
   [:p name]
   [:div.dots.is-flex-grow-2]
   [:p rating]])

(defn- details
  [{:keys [appearance
           comment
           overall
           notes
           smell
           taste]}]
  [:<>
   [:div.columns
    [:div.column.is-half
     [:h6 "Ratings"]
     [rating-row "Overall" overall true]
     [rating-row "Appearance" appearance false]
     [rating-row "Smell" smell false]
     [rating-row "Taste" taste false]]
    [:div.column.is-half
     [:h6 "Comments"]
     [:p.is-size-7 comment]]]
   (when (not-empty notes)
     [:div.mb-4.is-flex
      (for [note notes]
        ^{:key note}
        [:span.tag.is-small.is-primary.is-light.mr-2 note])])])

(defn card
  [_]
  (r/with-let [show-details? (r/atom false)]
    (fn [{{:keys [id
                  overall]
           :as drink}        :drink
          show-drink-modal!  :show-drink-modal!
          show-delete-modal! :show-delete-modal!}]
      [:div.box
       [:article.media
        [:div.media-content
         [overall-rating-badge overall]
         [summary drink]
         [:div.mb-2]

         (when @show-details?
           [details drink])

         [actions {:on-details-toggle #(swap! show-details? not)
                   :on-delete         #(show-delete-modal! id)
                   :on-edit           #(show-drink-modal! drink)
                   :show-details?     @show-details?}]]]])))
