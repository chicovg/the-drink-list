(ns the-beer-list.components.drink
  (:require
   [reagent.core :as r]
   [the-beer-list.components.common :as common]))

(defn- rating-class
  [value]
  (condp > value
    2 "rating__lt-2"
    3 "rating__lt-3"
    4 "rating__lt-4"
    "rating__lt-5"))

(defn- rating
  [value]
  [:span {:class (rating-class value)}
   (.toFixed value 1)])

(defn card
  [_]
  (r/with-let [show-details? (r/atom false)]
    (fn [{{:keys [id
                  appearance
                  comment
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
         [:p.is-size-7 [:strong "Overall: " [rating (common/calc-overall drink)]]]
         [:p.is-size-7
          [:span.mr-1 " Appearance: " [rating appearance]]
          [:span.mr-1 " Smell: " [rating smell]]
          [:span " Taste: " [rating taste]]]]

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

        [:div.buttons.mt-2.is-pulled-right
         [:button.button.is-outlined.is-circle.is-primary.is-small
          {:on-click #(show-drink-modal! drink)}
          [:span.icon
           [:i.fas.fa-edit]]]
         [:button.button.is-outlined.is-circle.is-danger.is-small
          [:span.icon
           [:i.fas.fa-trash]]]]]])))
