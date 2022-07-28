(ns the-drink-list.uix-components.drink
  (:require
   [the-drink-list.components.common :as common]
   [the-drink-list.uix-components.context :as context]
   [the-drink-list.uix-components.logged-at :as logged-at]
   [uix.core :refer [$ defui use-context use-state]]))

(defui overall-rating-badge
  [{:keys [overall]}]
  ($ :span.is-pulled-right.is-rounded.overall
     {:class (common/rating-class overall)}
     overall))

(defui summary
  [{:keys [created name maker style]}]
  ($ :<>
     ($ :p.is-clipped.mr-2
        ($ :strong name))
     ($ :p.mr-2.is-size-7 maker)
     ($ :p.is-size-7.is-italic style)
     ($ logged-at/logged-at {:date created})))

(defui actions
  [{:keys [on-details-toggle
           on-edit
           on-delete
           show-details?]}]
  ($ :nav.level.is-mobile
     ($ :div.level-left
        ($ :a.level-item {:on-click on-details-toggle}
           ($ :span.icon.is-small.has-text-info
              ($ :i.fas {:aria-hidden "true"
                         :class       (if show-details?
                                        "fa-chevron-up"
                                        "fa-chevron-down")}))))
     ($ :div.level-right
        ($ :a.level-item {:on-click on-edit}
           ($ :i.fas.fa-edit {:aria-hidden true}))
        ($ :a.level-item {:on-click on-delete}
           ($ :i.fas.fa-trash {:aria-hidden true})))))

(defui rating-row
  [{:keys [name rating bold?]}]
  ($ :div.is-flex.is-size-7
     {:class (when bold? "has-text-weight-bold")}
     ($ :p name)
     ($ :div.dots.is-flex-grow-2)
     ($ :p rating)))

(defui details
  [{:keys [appearance
           comment
           overall
           notes
           smell
           taste]}]
  ($ :<>
     ($ :div.columns
        ($ :div.column.is-half
           ($ :h6 "Ratings")
           ($ rating-row {:name   "Overall"
                          :rating overall
                          :bold?  true})
           ($ rating-row {:name   "Appearance"
                          :rating appearance})
           ($ rating-row {:name   "Smell"
                          :rating smell})
           ($ rating-row {:name   "Taste"
                          :rating taste}))
        ($ :div.column.is-half
           ($ :h6 "Comments")
           ($ :p.is-size-7 comment)))
     (when (not-empty notes)
       ($ :div.mb-4.is-flex
          (for [note notes]
            ($ :span.tag.is-small.is-primary.is-light.mr-2
               {:key note}
               note))))))

(defui card
  [{:keys [drink]}]
  (let [{:keys [show-drink-modal!
                show-delete-modal!]}      (use-context context/app)
        [show-details? set-show-details!] (use-state false)]
    ($ :div.box
       ($ :article.media
          ($ :div.media-content
             ($ overall-rating-badge drink)
             ($ summary drink)
             ($ :div.mb-2)

             (when show-details?
               ($ details drink))

             ($ actions {:on-details-toggle #(set-show-details! (not show-details?))
                         :on-delete         #(show-delete-modal! (:id drink))
                         :on-edit           #(show-drink-modal! drink)
                         :show-details?     show-details?}))))))
