(ns the-beer-list.components.drink
  (:require
   [reagent.core :as r]))

(defn- actions
  [{:keys [id]}]
  [:div.btn-group.float-right
   [:button.btn.btn-link
    [:i.icon.icon-edit]]
   [:button.btn.btn-link
    [:i.icon.icon-delete.text-error]]])

;; (defn- actions
;;   [{:keys [id]}]
;;   (r/with-let [active? (r/atom false)]
;;     [:div.dropdown.float-right
;;      {:class (when @active? "active")}
;;      [:button.btn.btn-link {:on-click #(swap! active? not)}
;;       [:i.icon.icon-more-vert]]
;;      [:ul.menu
;;       [:li [:button.btn.btn-link "Edit"]]
;;       [:li [:button.btn.btn-link.text-error "Delete"]]]]))

(defn- title
  [{:keys [name type style]}]
  [:div
   [:span.card-title.h5.margin-right-sm name]
   [:span.chip type]
   [:span.chip style]])

(defn- subtitle
  [{:keys [maker]}]
  [:div.card-subtitle.text-gray maker])

(defn- round
  [value]
  (.toFixed value 1))

(defn- rating
  [{:keys [id comment label strong? value]}]
  (let [rounded-value (round value)]
    [:div.rating
     [:span
      [:i.icon.icon-message.margin-right-sm.text-primary
       {:class (when-not comment "text-gray")}]
      [:label {:for id :class (when strong? "text-bold")} label]]
     [:span
      [:em.padding-right-sm rounded-value]
      ;; TODO different color per value
      ;; 1 red
      ;; 2 orange
      ;; 3 yellow
      ;; 4 yellogreen
      ;; 5 blue
      [:progress.progress.tooltip {:class        "color-1"
                                   :data-tooltip (str label " " rounded-value)
                                   :min          0
                                   :max          5
                                   :value        rounded-value}]]]))

(defn- calc-overall
  [{:keys [appearance smell taste]}]
  (/ (+ appearance smell taste taste taste)
     5))

(defn card
  "A component which renders the drink summary"
  [{:keys [appearance
           appearance-comment
           smell
           smell-comment
           taste
           taste-comment
           comment]
    :as props}]
  [:div.beer-card.card
   [:div.card-header
    [actions props]
    [title props]
    [subtitle props]]
   [:div.card-body
    [rating {:id      :appearance
             :comment appearance-comment
             :label   "Appearance"
             :value   appearance}]
    [rating {:id      :smell
             :comment smell-comment
             :label  "Smell"
             :value   smell}]
    [rating {:id      :taste
             :comment taste-comment
             :label   "Taste"
             :value   taste}]
    [rating {:id      :overall
             :comment comment
             :label   "Overall"
             :value   (calc-overall props)
             :strong? true}]]])
