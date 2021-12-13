(ns the-beer-list.components.logged-at-stories
  (:require [the-beer-list.components.logged-at :as logged-at]
            [reagent.core :as r]))

(def ^:export default
  #js {:title     "Logged At Component"
       :component (r/reactify-component logged-at/logged-at)})

(defn- date-minus-millis
  [millis]
  (js/Date.
   (- (js/Date.)
      millis)))

(def examples (map date-minus-millis [(* 1000 30) ; 30 seconds
                                      (* 1000 60) ; 1 minute
                                      (* 1000 60 25) ; 25 minutes
                                      (* 1000 60 59) ; 59 minutes
                                      (* 1000 60 60) ; 1 hour
                                      (* 1000 60 60 7) ; 7 hours
                                      (* 1000 60 60 24) ; 1 day
                                      (* 1000 60 60 24 3) ; 3 days
                                      (* 1000 60 60 24 13) ; 1 week
                                      (* 1000 60 60 24 22) ; 3 weeks
                                      (* 1000 60 60 24 30) ; 1 month
                                      (* 1000 60 60 24 30 5) ; 5 months
                                      (* 1000 60 60 24 365) ; 1 year
                                      (* 1000 60 60 24 365 3) ; 3 years
                                      ]))

(defn all-examples
  []
  [:div
   (for [example examples]
     ^{:key example}
     [:div
      [:p.is-size-7 [:strong "Time: "] (.toString example)]
      [logged-at/logged-at example]])])

(defn ^:export AllExamples []
  (r/as-element [all-examples]))
