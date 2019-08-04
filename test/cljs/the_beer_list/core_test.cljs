(ns the-beer-list.core-test
  (:require [cljs.test :refer-macros [deftest is]]
            [re-frame.core :as rf]
            [day8.re-frame.test :as rf-test]
            [the-beer-list.events :as events]
            [the-beer-list.subs :as subs]))

;; user

(deftest test-init-user
  (rf-test/run-test-sync
   (let [is-logged-in? (rf/subscribe [::subs/is-logged-in?])
         user (rf/subscribe [::subs/user])]
     (rf/dispatch [::events/initialize-db])
     (is (not @is-logged-in?))
     (is (nil? @user)))))

(deftest test-set-user
  (rf-test/run-test-sync
   (let [is-logged-in? (rf/subscribe [::subs/is-logged-in?])
         user (rf/subscribe [::subs/user])
         on-snapshot (atom 0)]
     (rf/dispatch [::events/initialize-db])
     (rf/reg-fx :firestore/on-snapshot #(swap! on-snapshot inc))
     (rf/dispatch [::events/set-user {:id "foo"}])
     (is @is-logged-in?)
     (is {:id "foo"} @user)
     (is 1 @on-snapshot))))

;; nav

(deftest test-init-active-panel
  (rf-test/run-test-sync
   (rf/dispatch [::events/initialize-db])
   (let [active-panel (rf/subscribe [::subs/active-panel])]
     (is (nil? @active-panel)))))

(deftest test-set-active-panel
  (rf-test/run-test-sync
   (let [active-panel (rf/subscribe [::subs/active-panel])]
     (rf/dispatch [::events/initialize-db])
     (rf/dispatch [::events/set-active-panel :foo])
     (is :foo @active-panel))))

;; beer data

(deftest test-init-beer
  (rf-test/run-test-sync
   (rf/dispatch [::events/initialize-db])
   (let [beers (rf/subscribe [::subs/beers])]
     (is (empty? @beers)))))

(def firestore-beer-data {:docs [{:id "beer1"
                                  :data {"name" "Nice Ale"
                                         "brewery" "Nice"
                                         "type" "Ale"
                                         "rating" 3
                                         "comment" "Test data"
                                         "uuid" "iuthn34"}},
                                 {:id "beer2"
                                  :data {"name" "Good Gose"
                                         "brewery" "Good"
                                         "type" "Gose"
                                         "rating" 4
                                         "comment" "Test data"
                                         "uuid" "iuthn34"}}]})

(def beers-list [{:id "beer2"
                   :name "Good Gose"
                   :brewery "Good"
                   :type "Gose"
                   :rating 4
                   :comment "Test data"
                   :uuid "iuthn34"},
                  {:id "beer1"
                   :name "Nice Ale"
                   :brewery "Nice"
                   :type "Ale"
                   :rating 3
                   :comment "Test data"
                   :uuid "iuthn34"}])

(deftest test-set-beer-map
  (rf-test/run-test-sync
   (let [beers (rf/subscribe [::subs/beers])]
     (rf/dispatch [::events/initialize-db])
     (rf/dispatch [::events/set-beer-map firestore-beer-data])
     (is beers-list @beers))))

(deftest test-clear-beer-map
  (rf-test/run-test-sync
   (let [beers (rf/subscribe [::subs/beers])]
     (rf/dispatch [::events/initialize-db])
     (rf/dispatch [::events/set-beer-map firestore-beer-data])
     (rf/dispatch [::events/clear-beer-map])
     (is (empty? @beers)))))

(deftest test-set-beer-list-filter
  (rf-test/run-test-sync
   (let [beers (rf/subscribe [::subs/beers])]
     (rf/dispatch [::events/initialize-db])
     (rf/dispatch [::events/set-beer-map firestore-beer-data])
     (rf/dispatch [::events/set-beer-list-filter "Nice Ale"])
     (is 1 (count @beers))
     (is (last beers-list) (first @beers)))))

;; beer modal state

(deftest test-update-beer-modal-state
  (rf-test/run-test-sync
   (let [beer-modal-state (rf/subscribe [::subs/beer-modal-state])
         beer-modal-showing? (rf/subscribe [::subs/beer-modal-showing?])
         save-failed? (rf/subscribe [::subs/save-failed?])
         beer-modal-name-error (rf/subscribe [::subs/beer-modal-field-error :name])
         beer-modal-brewery-error (rf/subscribe [::subs/beer-modal-field-error :brewery])
         beer-modal-type-error (rf/subscribe [::subs/beer-modal-field-error :type])]
     (rf/dispatch [::events/initialize-db])
     (is :ready @beer-modal-state)
     (is (not @beer-modal-showing?))
     (is (not @save-failed?))
     (is (and (nil? @beer-modal-name-error)
              (nil? @beer-modal-brewery-error)
              (nil? @beer-modal-type-error)))

     (rf/dispatch [::events/update-beer-modal-state :show])
     (is :showing @beer-modal-state)
     (is @beer-modal-showing?)
     (is (not @save-failed?))
     (is (and (nil? @beer-modal-name-error)
              (nil? @beer-modal-brewery-error)
              (nil? @beer-modal-type-error)))

     (rf/dispatch [::events/update-beer-modal-state :save-no-name])
     (is :name-required @beer-modal-state)
     (is @beer-modal-showing?)
     (is (not @save-failed?))
     (is (and (nil? @beer-modal-brewery-error)
              (nil? @beer-modal-type-error)))
     (is "Name required" @beer-modal-name-error)

     (rf/dispatch [::events/update-beer-modal-state :field-changed])
     (is :showing @beer-modal-state)
     (is @beer-modal-showing?)
     (is (not @save-failed?))
     (is (and (nil? @beer-modal-name-error)
              (nil? @beer-modal-brewery-error)
              (nil? @beer-modal-type-error)))

     (rf/dispatch [::events/update-beer-modal-state :save-no-brewery])
     (is :brewery-required @beer-modal-state)
     (is @beer-modal-showing?)
     (is (not @save-failed?))
     (is (and (nil? @beer-modal-name-error)
              (nil? @beer-modal-type-error)))
     (is "Brewery required" @beer-modal-brewery-error)

     (rf/dispatch [::events/update-beer-modal-state :field-changed])
     (is :showing @beer-modal-state)
     (is @beer-modal-showing?)
     (is (not @save-failed?))
     (is (and (nil? @beer-modal-name-error)
              (nil? @beer-modal-brewery-error)
              (nil? @beer-modal-type-error)))

     (rf/dispatch [::events/update-beer-modal-state :save-no-type])
     (is :type-required @beer-modal-state)
     (is @beer-modal-showing?)
     (is (not @save-failed?))
     (is (and (nil? @beer-modal-name-error)
              (nil? @beer-modal-brewery-error)))
     (is "Type required" @beer-modal-type-error)

     (rf/dispatch [::events/update-beer-modal-state :field-changed])
     (is :showing @beer-modal-state)
     (is @beer-modal-showing?)
     (is (not @save-failed?))
     (is (and (nil? @beer-modal-name-error)
              (nil? @beer-modal-brewery-error)
              (nil? @beer-modal-type-error)))

     (rf/dispatch [::events/update-beer-modal-state :try-save])
     (is :saving @beer-modal-state)
     (is @beer-modal-showing?)
     (is (not @save-failed?))
     (is (and (nil? @beer-modal-name-error)
              (nil? @beer-modal-brewery-error)
              (nil? @beer-modal-type-error)))

     (rf/dispatch [::events/update-beer-modal-state :firestore-failure])
     (is :save-failed @beer-modal-state)
     (is @beer-modal-showing?)
     (is @save-failed?)
     (is (and (nil? @beer-modal-name-error)
              (nil? @beer-modal-brewery-error)
              (nil? @beer-modal-type-error)))

     (rf/dispatch [::events/update-beer-modal-state :try-save])
     (is :saving @beer-modal-state)
     (is @beer-modal-showing?)
     (is (not @save-failed?))
     (is (and (nil? @beer-modal-name-error)
              (nil? @beer-modal-brewery-error)
              (nil? @beer-modal-type-error)))

     (rf/dispatch [::events/update-beer-modal-state :firestore-success])
     (is :ready @beer-modal-state)
     (is (not @beer-modal-showing?))
     (is (not @save-failed?))
     (is (and (nil? @beer-modal-name-error)
              (nil? @beer-modal-brewery-error)
              (nil? @beer-modal-type-error)))

     (rf/dispatch [::events/update-beer-modal-state :show])
     (rf/dispatch [::events/update-beer-modal-state :hide])
     (is :ready @beer-modal-state)
     (is (not @beer-modal-showing?))
     (is (not @save-failed?))
     (is (and (nil? @beer-modal-name-error)
              (nil? @beer-modal-brewery-error)
              (nil? @beer-modal-type-error)))

     (rf/dispatch [::events/update-beer-modal-state :show])
     (rf/dispatch [::events/update-beer-modal-state :try-save])
     (rf/dispatch [::events/update-beer-modal-state :firestore-failure])
     (rf/dispatch [::events/update-beer-modal-state :hide])
     (is :ready @beer-modal-state)
     (is (not @beer-modal-showing?))
     (is (not @save-failed?))
     (is (and (nil? @beer-modal-name-error)
              (nil? @beer-modal-brewery-error)
              (nil? @beer-modal-type-error)))

     (rf/dispatch [::events/update-beer-modal-state :show])
     (rf/dispatch [::events/update-beer-modal-state :save-no-name])
     (rf/dispatch [::events/update-beer-modal-state :hide])
     (is :ready @beer-modal-state)
     (is (not @beer-modal-showing?))
     (is (not @save-failed?))
     (is (and (nil? @beer-modal-name-error)
              (nil? @beer-modal-brewery-error)
              (nil? @beer-modal-type-error)))

     (rf/dispatch [::events/update-beer-modal-state :show])
     (rf/dispatch [::events/update-beer-modal-state :save-no-brewery])
     (rf/dispatch [::events/update-beer-modal-state :hide])
     (is :ready @beer-modal-state)
     (is (not @beer-modal-showing?))
     (is (not @save-failed?))
     (is (and (nil? @beer-modal-name-error)
              (nil? @beer-modal-brewery-error)
              (nil? @beer-modal-type-error)))

     (rf/dispatch [::events/update-beer-modal-state :show])
     (rf/dispatch [::events/update-beer-modal-state :save-no-type])
     (rf/dispatch [::events/update-beer-modal-state :hide])
     (is :ready @beer-modal-state)
     (is (not @beer-modal-showing?))
     (is (not @save-failed?))
     (is (and (nil? @beer-modal-name-error)
              (nil? @beer-modal-brewery-error)
              (nil? @beer-modal-type-error))))))

(deftest test-show-add-beer-modal
  (rf-test/run-test-sync
   (let [beer-modal-beer (rf/subscribe [::subs/beer-modal-beer])
         beer-modal-is-adding? (rf/subscribe [::subs/beer-modal-is-adding?])]
     (rf/dispatch [::events/initialize-db])
     (is {:rating 3} @beer-modal-beer)
     (is (not @beer-modal-is-adding?))
     (rf/dispatch [::events/show-add-beer-modal])
     (is {:name nil :brewery nil :type nil :rating 3 :comment nil} @beer-modal-beer)
     (is @beer-modal-is-adding?))))

(deftest test-show-edit-beer-modal
  (rf-test/run-test-sync
   (let [beer-modal-beer (rf/subscribe [::subs/beer-modal-beer])
         beer-modal-is-adding? (rf/subscribe [::subs/beer-modal-is-adding?])]
     (rf/dispatch [::events/initialize-db])
     (is {:rating 3} @beer-modal-beer)
     (is (not @beer-modal-is-adding?))

     (def beer {:name "A beer"
                :brewery "A brewery"
                :type "Ale"
                :rating 5
                :comment "It was good"})
     (rf/dispatch [::events/show-edit-beer-modal beer])
     (is beer @beer-modal-beer)
     (is (not @beer-modal-is-adding?)))))

(deftest test-clear-and-hide-beer-modal
  (rf-test/run-test-sync
   (let [beer-modal-beer (rf/subscribe [::subs/beer-modal-beer])
         beer-modal-is-adding? (rf/subscribe [::subs/beer-modal-is-adding?])]
     (rf/dispatch [::events/initialize-db])
     (rf/dispatch [::events/show-add-beer-modal])
     (is {:name nil :brewery nil :type nil :rating 3 :comment nil} @beer-modal-beer)
     (is @beer-modal-is-adding?)

     (rf/dispatch [::events/clear-and-hide-beer-modal])
     (is {:rating 3} @beer-modal-beer)
     (is (not @beer-modal-is-adding?)))))

(deftest test-set-beer-modal-value
  (rf-test/run-test-sync
   (let [beer-modal-beer (rf/subscribe [::subs/beer-modal-beer])
         beer-modal-name-value (rf/subscribe [::subs/beer-modal-field-value :name])
         beer-modal-brewery-value (rf/subscribe [::subs/beer-modal-field-value :brewery])
         beer-modal-type-value (rf/subscribe [::subs/beer-modal-field-value :type])
         beer-modal-rating-value (rf/subscribe [::subs/beer-modal-field-value :rating])
         beer-modal-comment-value (rf/subscribe [::subs/beer-modal-field-value :comment])]
     (rf/dispatch [::events/initialize-db])
     (= {:rating 3} @beer-modal-beer)
     (is (and (nil? @beer-modal-name-value)
              (nil? @beer-modal-brewery-value)
              (nil? @beer-modal-type-value)
              (nil? @beer-modal-comment-value)))
     (is 3 @beer-modal-rating-value)

     (def name-value "A beer")
     (rf/dispatch [::events/set-beer-modal-value :name name-value])
     (is {:name name-value} @beer-modal-beer)
     (is (and (nil? @beer-modal-brewery-value)
              (nil? @beer-modal-type-value)
              (nil? @beer-modal-comment-value)))
     (is name-value @beer-modal-name-value)
     (is 3 @beer-modal-rating-value)

     (def brewery-value "A brewery")
     (rf/dispatch [::events/set-beer-modal-value :brewery brewery-value])
     (is {:name name-value :brewery brewery-value} @beer-modal-beer)
     (is (and (nil? @beer-modal-type-value)
              (nil? @beer-modal-comment-value)))
     (is name-value @beer-modal-name-value)
     (is brewery-value @beer-modal-brewery-value)
     (is 3 @beer-modal-rating-value)

     (def type-value "A beer type")
     (rf/dispatch [::events/set-beer-modal-value :type type-value])
     (is {:name name-value :brewery brewery-value :type type-value} @beer-modal-beer)
     (is (nil? @beer-modal-comment-value))
     (is name-value @beer-modal-name-value)
     (is brewery-value @beer-modal-brewery-value)
     (is type-value @beer-modal-type-value)
     (is 3 @beer-modal-rating-value)

     (def rating-value 1)
     (rf/dispatch [::events/set-beer-modal-value :rating rating-value])
     (is {:name name-value :brewery brewery-value :type type-value :rating rating-value}
         @beer-modal-beer)
     (is (nil? @beer-modal-comment-value))
     (is name-value @beer-modal-name-value)
     (is brewery-value @beer-modal-brewery-value)
     (is type-value @beer-modal-type-value)
     (is rating-value @beer-modal-rating-value)

     (def comment-value "This is a beer")
     (rf/dispatch [::events/set-beer-modal-value :comment comment-value])
     (is {:name name-value :brewery brewery-value :type type-value :rating rating-value :comment comment-value}
        @beer-modal-beer)
     (is name-value @beer-modal-name-value)
     (is brewery-value @beer-modal-brewery-value)
     (is type-value @beer-modal-type-value)
     (is rating-value @beer-modal-rating-value)
     (is comment-value @beer-modal-comment-value))))

;; delete confirm state

(deftest test-update-delete-confirm-state
  (rf-test/run-test-sync
   (let [delete-confirm-state (rf/subscribe [::subs/delete-confirm-state])
         delete-confirm-modal-showing? (rf/subscribe [::subs/delete-confirm-modal-showing?])
         delete-failed? (rf/subscribe [::subs/delete-failed?])]
     (rf/dispatch [::events/initialize-db])
     (is :ready @delete-confirm-state)
     (is (not @delete-confirm-modal-showing?))
     (is (not @delete-failed?))

     (rf/dispatch [::events/update-delete-confirm-state :show])
     (is :showing @delete-confirm-state)
     (is @delete-confirm-modal-showing?)
     (is (not @delete-failed?))

     (rf/dispatch [::events/update-delete-confirm-state :try-delete])
     (is :deleting @delete-confirm-state)
     (is @delete-confirm-modal-showing?)
     (is (not @delete-failed?))

     (rf/dispatch [::events/update-delete-confirm-state :firestore-failure])
     (is :delete-failed @delete-confirm-state)
     (is @delete-confirm-modal-showing?)
     (is @delete-failed?)

     (rf/dispatch [::events/update-delete-confirm-state :try-delete])
     (is :deleting @delete-confirm-state)
     (is @delete-confirm-modal-showing?)
     (is (not @delete-failed?))

     (rf/dispatch [::events/update-delete-confirm-state :firestore-success])
     (is :ready @delete-confirm-state)
     (is (not @delete-confirm-modal-showing?))
     (is (not @delete-failed?))

     (rf/dispatch [::events/update-delete-confirm-state :show])
     (rf/dispatch [::events/update-delete-confirm-state :hide])
     (is :ready @delete-confirm-state)
     (is (not @delete-confirm-modal-showing?))
     (is (not @delete-failed?))

     (rf/dispatch [::events/update-delete-confirm-state :show])
     (rf/dispatch [::events/update-delete-confirm-state :try-delete])
     (rf/dispatch [::events/update-delete-confirm-state :firestore-failure])
     (rf/dispatch [::events/update-delete-confirm-state :hide])
     (is :ready @delete-confirm-state)
     (is (not @delete-confirm-modal-showing?))
     (is (not @delete-failed?))

     (rf/dispatch [::events/update-delete-confirm-state :invalid-state])
     (is :ready @delete-confirm-state)
     (is (not @delete-confirm-modal-showing?))
     (is (not @delete-failed?)))))

(deftest test-delete-beer
  (rf-test/run-test-sync
   (let [delete-calls (atom 0)
         delete-confirm-state (rf/subscribe [::subs/delete-confirm-state])]
     (rf/reg-fx :firestore/delete #(swap! delete-calls inc))
     (rf/dispatch [::events/initialize-db])
     (rf/dispatch [::events/update-delete-confirm-state :show])
     (rf/dispatch [::events/delete-beer 1])
     (is :deleting @delete-confirm-state)
     (is 1 @delete-calls))))

(deftest test-delete-beer-locally
  (rf-test/run-test-sync
   (let [beers (rf/subscribe [::subs/beers])]
     (rf/dispatch [::events/initialize-db])
     (rf/dispatch [::events/set-beer-map firestore-beer-data])
     (is beers-list @beers)
     (rf/dispatch [::events/delete-beer-locally "beer1"])
     (is 1 (count @beers))
     (is (first beers-list) (first @beers)))))

;; loading modal state

(deftest test-update-loading-modal-state
  (rf-test/run-test-sync
   (let [loading-modal-state (rf/subscribe [::subs/loading-modal-state])
         loading-modal-showing? (rf/subscribe [::subs/loading-modal-showing?])]
     (rf/dispatch [::events/initialize-db])
     (is :ready @loading-modal-state)
     (is (not @loading-modal-showing?))

     (rf/dispatch [::events/update-loading-modal-state :show])
     (is :showing @loading-modal-state)
     (is @loading-modal-showing?)

     (rf/dispatch [::events/update-loading-modal-state :firestore-failure])
     (is :load-failed @loading-modal-state)
     (is (not @loading-modal-showing?))

     (rf/dispatch [::events/update-loading-modal-state :show])
     (is :showing @loading-modal-state)
     (is @loading-modal-showing?)

     (rf/dispatch [::events/update-loading-modal-state :hide])
     (is :ready @loading-modal-state)
     (is (not @loading-modal-showing?))

     (rf/dispatch [::events/update-loading-modal-state :invalid-state])
     (is :ready @loading-modal-state)
     (is (not @loading-modal-showing?)))))

;; login state

(deftest test-update-log-in-state
  (rf-test/run-test-sync
   (let [log-in-state (rf/subscribe [::subs/log-in-state])
         log-in-failed? (rf/subscribe [::subs/log-in-failed?])]
     (rf/dispatch [::events/initialize-db])
     (is :ready @log-in-state)
     (is (not @log-in-failed?))

     (rf/dispatch [::events/update-log-in-state :log-in])
     (is :logging-in @log-in-state)
     (is (not @log-in-failed?))

     (rf/dispatch [::events/update-log-in-state :no-user-received])
     (is :log-in-failed @log-in-state)
     (is @log-in-failed?)

     (rf/dispatch [::events/update-log-in-state :log-in])
     (is :logging-in @log-in-state)
     (is (not @log-in-failed?))

     (rf/dispatch [::events/update-log-in-state :user-received])
     (is :logged-in @log-in-state)
     (is (not @log-in-failed?))

     (rf/dispatch [::events/update-log-in-state :log-out])
     (is :logging-out @log-in-state)
     (is (not @log-in-failed?))

     (rf/dispatch [::events/update-log-in-state :no-user-received])
     (is :ready @log-in-state)
     (is (not @log-in-failed?))

     (rf/dispatch [::events/update-log-in-state :invalid-state])
     (is :ready @log-in-state)
     (is (not @log-in-failed?)))))

(deftest test-sign-in
  (rf-test/run-test-sync
   (let [loading-modal-state (rf/subscribe [::subs/loading-modal-state])
         log-in-state (rf/subscribe [::subs/log-in-state])
         sign-in-calls (atom 0)]
     (rf/dispatch [::events/initialize-db])
     (rf/reg-fx :firebase/google-sign-in #(swap! sign-in-calls inc))
     (is :showing @loading-modal-state)
     (is :logging-in @log-in-state)
     (is 1 @sign-in-calls))))

(deftest test-sign-out
  (rf-test/run-test-sync
   (let [log-in-state (rf/subscribe [::subs/log-in-state])
         sign-out-calls (atom 0)]
     (rf/reg-fx :firebase/sign-out #(swap! sign-out-calls inc))
     (rf/dispatch [::events/initialize-db])
     (rf/dispatch [::events/update-log-in-state :log-in])
     (rf/dispatch [::events/update-log-in-state :user-received])
     (is :logged-in @log-in-state)
     (rf/dispatch [::events/sign-out])
     (is :logging-out @log-in-state)
     (is 1 @sign-out-calls))))
