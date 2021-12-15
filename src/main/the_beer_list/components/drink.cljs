(ns the-beer-list.components.drink
  (:require
   [reagent.core :as r]
   [the-beer-list.components.common :as common]
   [the-beer-list.components.logged-at :as logged-at]))

(defn- overall-rating-badge
  [overall]
  [:span.is-small.is-pulled-right.is-rounded.overall
   {:class (common/rating-class overall)}
   overall])

(defn- actions
  [show-details?]
  [:nav.level.is-mobile
   [:div.level-left
    [:a.level-item {:on-click #(swap! show-details? not)}
     [:span.icon.is-small
      [:i.fas {:aria-hidden "true"
               :class       (if @show-details?
                              "fa-chevron-up"
                              "fa-chevron-down")}]]]
    [:a.level-item
     [:span.icon.is-small
      [:i.fas.fa-edit {:aria-hidden "true"}]]]
    [:a.level-item
     [:span.icon.is-small
      [:i.fas.fa-trash {:aria-hidden "true"}]]]]])

(defn card
  [_]
  (r/with-let [show-details? (r/atom false)]
    (fn [{{:keys [created
                  name
                  maker
                  overall
                  style]} :drink}]
      [:div.box
       [:article.media
        [:div.media-content
         [overall-rating-badge overall]
         [:p.is-clipped.mr-2 [:strong name]]
         [:p.mr-2.is-size-7 maker]
         [:p.is-size-7.is-italic style]
         [:p.is-size-8.is-italic [logged-at/logged-at created]]
         [:div.mb-2]

         ;; TODO collapsable details
         ;; other ratings
         ;; tags
         ;; comment
         ;; [:div.tags.mb-1.mt-1
         ;;  [:span.tag.is-small maker]
         ;;  [:span.tag.is-small.is-primary style]]

         [actions show-details?]]]])))

(defn- rating
  [value]
  [:span {:class (common/rating-class value)}
   (if value
     (.toFixed value 1)
     "-")])

#_(defn card
    [_]
    (r/with-let [show-details? (r/atom false)]
      (fn [{{:keys [id
                    overall
                    appearance
                    comment
                    created
                    maker
                    name
                    notes
                    smell
                    style
                    taste]
             :as drink}       :drink
            show-drink-modal! :show-drink-modal!}]
        [:article.media
         [:div.media-content.is-clipped
          [:p.is-clipped [:strong name]]

          [:div.tags.mb-1.mt-1
           [:span.tag.is-small maker]
           [:span.tag.is-small.is-primary style]]

          [:div.mt-1.mb-1
           [:p.is-size-7 [:strong "Overall: " [rating overall]]]
           [:p.is-size-7
            [:span.mr-1 " Appearance: " [rating appearance]]
            [:span.mr-1 " Smell: " [rating smell]]
            [:span " Taste: " [rating taste]]]]

          [logged-at/logged-at created]

          (when @show-details?
            [:div.mt-2
             (when (not-empty comment)
               [:div.p-1 [:p.is-size-7 comment]])

             (when (not-empty notes)
               [:div
                (for [note notes]
                  ^{:key note}
                  [:span.tag.is-small.is-primary.is-light note])])])

          (when (or (not-empty comment) (not-empty notes))
            [:div.mt-1
             [:button.button.is-ghost.is-small.p-0.ml-1
              {:on-click #(swap! show-details? not)}
              (if @show-details?
                "Less..."
                "More...")]])

          [:div.buttons.mt-2
           [:button.button.is-outlined.is-circle.is-primary.is-small
            {:on-click #(show-drink-modal! drink)}
            [:span.icon
             [:i.fas.fa-edit]]]
           [:button.button.is-outlined.is-circle.is-danger.is-small
            [:span.icon
             [:i.fas.fa-trash]]]]]])))
