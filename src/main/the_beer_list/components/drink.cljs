(ns the-beer-list.components.drink
  (:require
   [reagent.core :as r]
   [the-beer-list.components.common :as common]))

(defn- is-collapsed?
  [p]
  (when p
    (or (< (.-offsetHeight p) (.-scrollHeight p))
        (< (.-offsetWidth p) (.-scrollWidth p)))))

(defn- comment-paragraph
  [_]
  (r/with-let [p-ref      (atom nil)
               collapsed? (r/atom false)
               expanded?  (r/atom false)]
    (r/create-class
     {:component-did-mount
      (fn []
        (reset! collapsed? (is-collapsed? @p-ref)))
      :reagent-render
      (fn [comment]
        (when (not-empty comment)
          [:div.p-1
           [:div.mt-1.mb-1
            [:p.is-size-7 {:class (when-not @expanded? "line-clamp-3")
                           :ref   #(reset! p-ref %)}
             comment]]
           (when @collapsed?
             [:button.button.is-small.is-ghost
              {:on-click #(swap! expanded? not)}
              (if @expanded?
                "Read Less"
                "Read More")])]))})))

(defn- rating-class
  [value]
  (condp > value
    2 "rating__lt-2"
    3 "rating__lt-3"
    4 "rating__lt-4"
    "rating__lt-5"))

(defn rating
  [value]
  [:span {:class (rating-class value)}
   (.toFixed value 1)])

(defn card
  [{:keys [id appearance comment maker name notes smell style taste] :as props}]
  [:article.media.has-background-white
   [:div.media-content
    [:p [:strong name]]
    [:div.tags.has-addons.mb-1.mt-1
     [:span.tag.is-small maker]
     [:span.tag.is-small.is-primary style]]
    [:div.mt-1.mb-1
     [:p.is-size-7
      [:span.mr-1
       [:strong [:em "Overall: "] [rating (common/calc-overall props)]]]
      [:span.mr-1
       [:em " Appearance: "] [rating appearance]]
      [:span.mr-1
       [:em " Smell: "] [rating smell]]
      [:span
       [:em " Taste: "] [rating taste]]]]
    [comment-paragraph comment]
    (when (not-empty notes)
      [:div
       (for [note notes]
         ^{:key note}
         [:span.tag.is-small.is-primary.is-light note])])]])
