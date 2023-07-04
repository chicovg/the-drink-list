(ns the-drink-list.content-cards
  (:require
   [devcards.core :as dc]
   [the-drink-list.uix-components.logged-at :as logged-at]
   [uix.core :refer [$ defui]]))

(declare drink-list)

(dc/defcard
  drink-list
  "TODO")

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

(defui logged-at-preview []
  ($ :div
     (for [example examples]
       ($ :div {:key example}
          ($ :p.is-size-7
             ($ :strong "Time: ") (str example))
          ($ logged-at/logged-at {:date example})))))

(declare logged-at)

(dc/defcard logged-at
  logged-at-preview)

(declare navbar)

(dc/defcard
  navbar
  "TODO")

(declare options-nav)

(dc/defcard
  options-nav
  "TODO")
