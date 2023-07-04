(ns the-drink-list.content-cards
  (:require
   [devcards.core :as dc]
   [the-drink-list.types.drink :as drink]
   [the-drink-list.uix-components.context :as context]
   [the-drink-list.uix-components.drink-list :as drink-list]
   [the-drink-list.uix-components.logged-at :as logged-at]
   [the-drink-list.uix-components.navbar :as navbar]
   [the-drink-list.uix-components.options-nav :as options-nav]
   [the-drink-list.uix-components.state :as state]
   [uix.core :refer [$ defui]]))

(declare drink-list--empty)

(dc/defcard
  drink-list--empty
  ($ drink-list/drink-list))

(defui drink-list-preview
  []
  ($ (.-Provider context/app) {:value {:drinks (drink/gen-drinks 5)
                                       :search-term ""
                                       :sort-state  {:asc?  false
                                                     :field :date}}}
     ($ drink-list/drink-list)))

(declare drink-list)

(dc/defcard
  drink-list
  ($ drink-list-preview))

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

(declare navbar--logged-out)

(dc/defcard
  navbar--logged-out
  ($ navbar/navbar))

(declare navbar)

(dc/defcard
  navbar
  ($ navbar/navbar {:user {:uid "abc123"}}))

(defui options-nav-preview []
  ($ (.-Provider context/app) {:value (state/use-app-state)}
     ($ options-nav/options-nav)))

(declare options-nav)

(dc/defcard
  options-nav
  ($ options-nav-preview))
