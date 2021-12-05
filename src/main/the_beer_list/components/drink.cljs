(ns the-beer-list.components.drink
  (:require
   [reagent.core :as r]))

(defn- header
  [{:keys [name maker style type]}]
  [:header.card-header
   [:p.card-header-title
    [:p name]
    [:span.is-flex.is-align-items-center.has-text-weight-normal
     [:p.is-size-7 maker]
     [:span.tag.is-light.is-rounded.ml-1 type]
     [:span.tag.is-light.is-rounded.ml-1 style]]]])

(defn- progress-class
  [value]
  (condp > value
    2 "is-danger"
    3 "is-warning"
    4 "is-primary"
    "is-success"))

(defn- rating
  [{:keys [id label overall? value]}]
  (when-let [rounded-value (some-> value (.toFixed 1))]
    [:div.rating.is-flex.is-justify-content-space-between
     [:span
      [:label {:for id :class (when overall? "has-text-weight-bold")} label]]
     [:span
      [:em {:class (when overall? "has-text-weight-bold")}
       rounded-value]
      [:progress.progress.is-small {:class        (progress-class rounded-value)
                                    :data-tooltip (str label " " rounded-value)
                                    :min          0
                                    :max          5
                                    :value        rounded-value}]]]))

(defn- calc-overall
  [{:keys [appearance smell taste]}]
  (/ (+ appearance smell taste taste taste)
     5))

(defn- content
  [{:keys [appearance notes smell taste]
    :as props}]
  [:div.card-content
   [:div.content
    [rating {:id      :overall
             :label   "Overall"
             :value   (calc-overall props)
             :overall? true}]
    [rating {:id      :appearance
             :label   "Appearance"
             :value   appearance}]
    [rating {:id      :smell
             :label  "Smell"
             :value   smell}]
    [rating {:id      :taste
             :label   "Taste"
             :value   taste}]
    [:div.mt-2
     (for [note notes]
       ^{:key note}
       [:div.tag.is-primay.is-light.mr-1
        note])]]])

(defn- footer
  [{:keys [id]}]
  [:footer.card-footer
   [:button.button.card-footer-item.is-info.is-inverted "Edit"]
   [:button.button.card-footer-item.is-danger.is-inverted "Delete"]])

(defn- display-card
  [props]
  [:div.beer-card.card
   [header props]
   [content props]
   [footer props]])

(defn- editing-card
  [props]
  [:div.beer-card.card
   [:div.card-body
    [:form
     [:div.form-group
      [:label.form-label {:for :name} "Name"]
      [:input.form-input {:id :name}]]]]])

(defn card
  "A component which renders drink fields or renders a form for editing a drink"
  [{:keys [editing?] :as props}]
  (if editing?
    [editing-card props]
    [display-card props]))
