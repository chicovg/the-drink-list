(ns the-beer-list.core-test
  (:require [cljs.test :refer-macros [is testing]]
            [devcards.core :refer-macros [deftest]]
            [re-frame.core :as rf]
            [day8.re-frame.test :as rf-test]
            [the-beer-list.events :as events]
            [the-beer-list.subs :as subs]))

;; user

(deftest test-init-user
  (testing "The user in initialized to nil"
    (rf-test/run-test-sync
     (let [is-logged-in? (rf/subscribe [::subs/is-logged-in?])
           user (rf/subscribe [::subs/user])]
       (rf/dispatch [::events/initialize-db])
       (is (not @is-logged-in?))
       (is (nil? @user))))))

(deftest test-set-user
  (testing "The user can be set when logged in"
    (rf-test/run-test-sync
     (let [is-logged-in? (rf/subscribe [::subs/is-logged-in?])
           user (rf/subscribe [::subs/user])
           on-snapshot (atom 0)]
       (rf/dispatch [::events/initialize-db])
       (rf/reg-fx :firestore/on-snapshot #(swap! on-snapshot inc))
       (rf/dispatch [::events/set-user {:id "foo"}])
       (is @is-logged-in?)
       (is {:id "foo"} @user)
       (is 1 @on-snapshot)))))

(deftest test-set-user-log-out
  (testing "The user is cleared when logging out"
    (rf-test/run-test-sync
     (let [is-logged-in? (rf/subscribe [::subs/is-logged-in?])
           user (rf/subscribe [::subs/user])
           on-snapshot (atom 0)]
       (rf/dispatch [::events/initialize-db])
       (rf/reg-fx :firestore/on-snapshot #(swap! on-snapshot inc))
       (rf/dispatch [::events/set-user {:id "foo"}])
       (rf/dispatch [::events/set-user nil])
       (is (not @is-logged-in?))
       (is (nil? @user))
       (is 1 @on-snapshot)))))

;; nav

(deftest test-init-active-panel
  (testing "The active panel is initialized to nil"
    (rf-test/run-test-sync
     (let [active-panel (rf/subscribe [::subs/active-panel])]
       (rf/dispatch [::events/initialize-db])
       (is (nil? @active-panel))))))

(deftest test-set-active-panel
  (testing "The active panel can be set"
    (rf-test/run-test-sync
     (let [active-panel (rf/subscribe [::subs/active-panel])]
       (rf/dispatch [::events/initialize-db])
       (rf/dispatch [::events/set-active-panel :foo])
       (is (= :foo @active-panel))))))

;; beer data

(deftest test-init-beer-map
  (testing "The beer data is initialized as an empty collection"
    (rf-test/run-test-sync
     (rf/dispatch [::events/initialize-db])
     (let [beers (rf/subscribe [::subs/beers])]
       (is (empty? @beers))))))

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
  (testing "the beer map can be set"
    (rf-test/run-test-sync
     (let [beers (rf/subscribe [::subs/beers])]
       (rf/dispatch [::events/initialize-db])
       (rf/dispatch [::events/set-beer-map firestore-beer-data])
       (is (= beers-list @beers))))))

(deftest test-clear-beer-map
  (testing "The beer map can be cleared"
    (rf-test/run-test-sync
     (let [beers (rf/subscribe [::subs/beers])]
       (rf/dispatch [::events/initialize-db])
       (rf/dispatch [::events/set-beer-map firestore-beer-data])
       (rf/dispatch [::events/clear-beer-map])
       (is (empty? @beers))))))

(deftest test-set-beer-list-filter
  (testing "The beer list filter can be set to filter beers"
    (rf-test/run-test-sync
     (let [beers (rf/subscribe [::subs/beers])]
       (rf/dispatch [::events/initialize-db])
       (rf/dispatch [::events/set-beer-map firestore-beer-data])
       (rf/dispatch [::events/set-beer-list-filter "Nice Ale"])
       (is 1 (count @beers))
       (is (= (last beers-list) (first @beers)))))))

;; ;; beer modal state

(deftest test-update-beer-form-state
  (testing "The beer modal state is transitioned correctly"
    (rf-test/run-test-sync
     (let [beer-form-state (rf/subscribe [::subs/beer-form-state])
           is-saving? (rf/subscribe [::subs/is-saving?])
           save-failed? (rf/subscribe [::subs/save-failed?])
           beer-form-name-error (rf/subscribe [::subs/beer-form-field-error :name])
           beer-form-brewery-error (rf/subscribe [::subs/beer-form-field-error :brewery])
           beer-form-type-error (rf/subscribe [::subs/beer-form-field-error :type])]
       (rf/dispatch [::events/initialize-db])
       (is (= :ready @beer-form-state))
       (is (not @is-saving?))
       (is (not @save-failed?))
       (is (and (nil? @beer-form-name-error)
                (nil? @beer-form-brewery-error)
                (nil? @beer-form-type-error)))

       (rf/dispatch [::events/update-beer-form-state :save-no-name])
       (is (= :name-required @beer-form-state))
       (is (not @is-saving?))
       (is (not @save-failed?))
       (is (and (nil? @beer-form-brewery-error)
                (nil? @beer-form-type-error)))
       (is "Name required" @beer-form-name-error)

       (rf/dispatch [::events/update-beer-form-state :field-changed])
       (is (= :ready @beer-form-state))
       (is (not @is-saving?))
       (is (not @save-failed?))
       (is (and (nil? @beer-form-name-error)
                (nil? @beer-form-brewery-error)
                (nil? @beer-form-type-error)))

       (rf/dispatch [::events/update-beer-form-state :save-no-brewery])
       (is (= :brewery-required @beer-form-state))
       (is (not @is-saving?))
       (is (not @save-failed?))
       (is (and (nil? @beer-form-name-error)
                (nil? @beer-form-type-error)))
       (is "Brewery required" @beer-form-brewery-error)

       (rf/dispatch [::events/update-beer-form-state :field-changed])
       (is (= :ready @beer-form-state))
       (is (not @is-saving?))
       (is (not @save-failed?))
       (is (and (nil? @beer-form-name-error)
                (nil? @beer-form-brewery-error)
                (nil? @beer-form-type-error)))

       (rf/dispatch [::events/update-beer-form-state :save-no-type])
       (is (= :type-required @beer-form-state))
       (is (not @is-saving?))
       (is (not @save-failed?))
       (is (and (nil? @beer-form-name-error)
                (nil? @beer-form-brewery-error)))
       (is "Type required" @beer-form-type-error)

       (rf/dispatch [::events/update-beer-form-state :field-changed])
       (is (= :ready @beer-form-state))
       (is (not @is-saving?))
       (is (not @save-failed?))
       (is (and (nil? @beer-form-name-error)
                (nil? @beer-form-brewery-error)
                (nil? @beer-form-type-error)))

       (rf/dispatch [::events/update-beer-form-state :try-save])
       (is (= :saving @beer-form-state))
       (is @is-saving?)
       (is (not @save-failed?))
       (is (and (nil? @beer-form-name-error)
                (nil? @beer-form-brewery-error)
                (nil? @beer-form-type-error)))

       (rf/dispatch [::events/update-beer-form-state :firestore-failure])
       (is (= :save-failed @beer-form-state))
       (is (not @is-saving?))
       (is @save-failed?)
       (is (and (nil? @beer-form-name-error)
                (nil? @beer-form-brewery-error)
                (nil? @beer-form-type-error)))

       (rf/dispatch [::events/update-beer-form-state :try-save])
       (is (= :saving @beer-form-state))
       (is @is-saving?)
       (is (not @save-failed?))
       (is (and (nil? @beer-form-name-error)
                (nil? @beer-form-brewery-error)
                (nil? @beer-form-type-error)))

       (rf/dispatch [::events/update-beer-form-state :firestore-success])
       (is (= :ready @beer-form-state))
       (is (not @is-saving?))
       (is (not @save-failed?))
       (is (and (nil? @beer-form-name-error)
                (nil? @beer-form-brewery-error)
                (nil? @beer-form-type-error)))

       (rf/dispatch [::events/update-beer-form-state :try-save])
       (rf/dispatch [::events/update-beer-form-state :firestore-failure])
       (rf/dispatch [::events/update-beer-form-state :hide])
       (is (= :ready @beer-form-state))
       (is (not @is-saving?))
       (is (not @save-failed?))
       (is (and (nil? @beer-form-name-error)
                (nil? @beer-form-brewery-error)
                (nil? @beer-form-type-error)))

       (rf/dispatch [::events/update-beer-form-state :save-no-name])
       (rf/dispatch [::events/update-beer-form-state :hide])
       (is (= :ready @beer-form-state))
       (is (not @is-saving?))
       (is (not @save-failed?))
       (is (and (nil? @beer-form-name-error)
                (nil? @beer-form-brewery-error)
                (nil? @beer-form-type-error)))

       (rf/dispatch [::events/update-beer-form-state :save-no-brewery])
       (rf/dispatch [::events/update-beer-form-state :hide])
       (is (= :ready @beer-form-state))
       (is (not @is-saving?))
       (is (not @save-failed?))
       (is (and (nil? @beer-form-name-error)
                (nil? @beer-form-brewery-error)
                (nil? @beer-form-type-error)))

       (rf/dispatch [::events/update-beer-form-state :save-no-type])
       (rf/dispatch [::events/update-beer-form-state :hide])
       (is (= :ready @beer-form-state))
       (is (not @is-saving?))
       (is (not @save-failed?))
       (is (and (nil? @beer-form-name-error)
                (nil? @beer-form-brewery-error)
                (nil? @beer-form-type-error)))))))

(deftest test-set-beer-form
  (testing "The beer form value can be set"
    (rf-test/run-test-sync
     (let [beer-form-beer (rf/subscribe [::subs/beer-form-beer])
           beers (rf/subscribe [::subs/beers])]
       (rf/dispatch [::events/initialize-db])
       (rf/dispatch [::events/set-beer-map firestore-beer-data])
       (rf/dispatch [::events/set-beer-form {:id "beer1"}])
       (is (= (second beers-list) @beer-form-beer))))))

(deftest test-set-beer-form-value
  (testing "The beer modal values can be set"
    (rf-test/run-test-sync
     (let [beer-form-beer (rf/subscribe [::subs/beer-form-beer])
           beer-form-name-value (rf/subscribe [::subs/beer-form-field-value :name])
           beer-form-brewery-value (rf/subscribe [::subs/beer-form-field-value :brewery])
           beer-form-type-value (rf/subscribe [::subs/beer-form-field-value :type])
           beer-form-rating-value (rf/subscribe [::subs/beer-form-field-value :rating])
           beer-form-comment-value (rf/subscribe [::subs/beer-form-field-value :comment])
           name-value "A beer"
           brewery-value "A brewery"
           type-value "A beer type"
           rating-value 1
           comment-value "This is a beer"]
       (rf/dispatch [::events/initialize-db])
       (is (= {:rating 3} @beer-form-beer))
       (is (and (nil? @beer-form-name-value)
                (nil? @beer-form-brewery-value)
                (nil? @beer-form-type-value)
                (nil? @beer-form-comment-value)))
       (is 3 @beer-form-rating-value)

       (rf/dispatch [::events/set-beer-form-value :name name-value])
       (is {:name name-value} @beer-form-beer)
       (is (and (nil? @beer-form-brewery-value)
                (nil? @beer-form-type-value)
                (nil? @beer-form-comment-value)))
       (is name-value @beer-form-name-value)
       (is 3 @beer-form-rating-value)

       (rf/dispatch [::events/set-beer-form-value :brewery brewery-value])
       (is {:name name-value :brewery brewery-value} @beer-form-beer)
       (is (and (nil? @beer-form-type-value)
                (nil? @beer-form-comment-value)))
       (is name-value @beer-form-name-value)
       (is brewery-value @beer-form-brewery-value)
       (is 3 @beer-form-rating-value)

       (rf/dispatch [::events/set-beer-form-value :type type-value])
       (is {:name name-value :brewery brewery-value :type type-value} @beer-form-beer)
       (is (nil? @beer-form-comment-value))
       (is name-value @beer-form-name-value)
       (is brewery-value @beer-form-brewery-value)
       (is type-value @beer-form-type-value)
       (is 3 @beer-form-rating-value)

       (rf/dispatch [::events/set-beer-form-value :rating rating-value])
       (is {:name name-value :brewery brewery-value :type type-value :rating rating-value}
           @beer-form-beer)
       (is (nil? @beer-form-comment-value))
       (is name-value @beer-form-name-value)
       (is brewery-value @beer-form-brewery-value)
       (is type-value @beer-form-type-value)
       (is rating-value @beer-form-rating-value)

       (rf/dispatch [::events/set-beer-form-value :comment comment-value])
       (is {:name name-value :brewery brewery-value :type type-value :rating rating-value :comment comment-value}
           @beer-form-beer)
       (is name-value @beer-form-name-value)
       (is brewery-value @beer-form-brewery-value)
       (is type-value @beer-form-type-value)
       (is rating-value @beer-form-rating-value)
       (is comment-value @beer-form-comment-value)))))

;; delete confirm state

(deftest test-update-delete-confirm-state
  (testing "The delete confirm modal state is transitioned correctly"
    (rf-test/run-test-sync
     (let [delete-confirm-state (rf/subscribe [::subs/delete-confirm-state])
           delete-confirm-modal-showing? (rf/subscribe [::subs/delete-confirm-modal-showing?])
           delete-failed? (rf/subscribe [::subs/delete-failed?])]
       (rf/dispatch [::events/initialize-db])
       (is (= :ready @delete-confirm-state))
       (is (not @delete-confirm-modal-showing?))
       (is (not @delete-failed?))

       (rf/dispatch [::events/update-delete-confirm-state :show])
       (is (= :showing @delete-confirm-state))
       (is @delete-confirm-modal-showing?)
       (is (not @delete-failed?))

       (rf/dispatch [::events/update-delete-confirm-state :try-delete])
       (is (= :deleting @delete-confirm-state))
       (is @delete-confirm-modal-showing?)
       (is (not @delete-failed?))

       (rf/dispatch [::events/update-delete-confirm-state :firestore-failure])
       (is (= :delete-failed @delete-confirm-state))
       (is @delete-confirm-modal-showing?)
       (is @delete-failed?)

       (rf/dispatch [::events/update-delete-confirm-state :try-delete])
       (is (= :deleting @delete-confirm-state))
       (is @delete-confirm-modal-showing?)
       (is (not @delete-failed?))

       (rf/dispatch [::events/update-delete-confirm-state :firestore-success])
       (is (= :ready @delete-confirm-state))
       (is (not @delete-confirm-modal-showing?))
       (is (not @delete-failed?))

       (rf/dispatch [::events/update-delete-confirm-state :show])
       (rf/dispatch [::events/update-delete-confirm-state :hide])
       (is (= :ready @delete-confirm-state))
       (is (not @delete-confirm-modal-showing?))
       (is (not @delete-failed?))

       (rf/dispatch [::events/update-delete-confirm-state :show])
       (rf/dispatch [::events/update-delete-confirm-state :try-delete])
       (rf/dispatch [::events/update-delete-confirm-state :firestore-failure])
       (rf/dispatch [::events/update-delete-confirm-state :hide])
       (is (= :ready @delete-confirm-state))
       (is (not @delete-confirm-modal-showing?))
       (is (not @delete-failed?))

       (rf/dispatch [::events/update-delete-confirm-state :invalid-state])
       (is (= :ready @delete-confirm-state))
       (is (not @delete-confirm-modal-showing?))
       (is (not @delete-failed?))))))

(deftest test-delete-beer
  (testing "state is updated and delete effect is called on delete"
    (rf-test/run-test-sync
     (let [delete-calls (atom 0)
           delete-confirm-state (rf/subscribe [::subs/delete-confirm-state])]
       (rf/reg-fx :firestore/delete #(swap! delete-calls inc))
       (rf/dispatch [::events/initialize-db])
       (rf/dispatch [::events/update-delete-confirm-state :show])
       (rf/dispatch [::events/delete-beer 1])
       (is (= :deleting @delete-confirm-state))
       (is 1 @delete-calls)))))

(deftest test-delete-beer-locally
  (testing "beer data is deleted locally"
    (rf-test/run-test-sync
     (let [beers (rf/subscribe [::subs/beers])]
       (rf/dispatch [::events/initialize-db])
       (rf/dispatch [::events/set-beer-map firestore-beer-data])
       (is (= beers-list @beers))
       (rf/dispatch [::events/delete-beer-locally "beer1"])
       (is 1 (count @beers))
       (is (first beers-list) (first @beers))))))

;; loading modal state

(deftest test-update-loading-modal-state
  (testing "Loading modal state is transitioned correctly"
    (rf-test/run-test-sync
     (let [loading-modal-state (rf/subscribe [::subs/loading-modal-state])
           loading-modal-showing? (rf/subscribe [::subs/loading-modal-showing?])]
       (rf/dispatch [::events/initialize-db])
       (is (= :ready @loading-modal-state))
       (is (not @loading-modal-showing?))

       (rf/dispatch [::events/update-loading-modal-state :show])
       (is (= :showing @loading-modal-state))
       (is @loading-modal-showing?)

       (rf/dispatch [::events/update-loading-modal-state :firestore-failure])
       (is (= :load-failed @loading-modal-state))
       (is (not @loading-modal-showing?))

       (rf/dispatch [::events/update-loading-modal-state :show])
       (is (= :showing @loading-modal-state))
       (is @loading-modal-showing?)

       (rf/dispatch [::events/update-loading-modal-state :hide])
       (is (= :ready @loading-modal-state))
       (is (not @loading-modal-showing?))

       (rf/dispatch [::events/update-loading-modal-state :invalid-state])
       (is (= :ready @loading-modal-state))
       (is (not @loading-modal-showing?))))))

;; login state

(deftest test-update-log-in-state
  (testing "Log in state is transitioned correctly"
    (rf-test/run-test-sync
     (let [log-in-state (rf/subscribe [::subs/log-in-state])
           log-in-failed? (rf/subscribe [::subs/log-in-failed?])]
       (rf/dispatch [::events/initialize-db])
       (is (= :ready @log-in-state))
       (is (not @log-in-failed?))

       (rf/dispatch [::events/update-log-in-state :log-in])
       (is (= :logging-in @log-in-state))
       (is (not @log-in-failed?))

       (rf/dispatch [::events/update-log-in-state :no-user-received])
       (is (= :log-in-failed @log-in-state))
       (is @log-in-failed?)

       (rf/dispatch [::events/update-log-in-state :log-in])
       (is (= :logging-in @log-in-state))
       (is (not @log-in-failed?))

       (rf/dispatch [::events/update-log-in-state :user-received])
       (is (= :logged-in @log-in-state))
       (is (not @log-in-failed?))

       (rf/dispatch [::events/update-log-in-state :log-out])
       (is (= :logging-out @log-in-state))
       (is (not @log-in-failed?))

       (rf/dispatch [::events/update-log-in-state :no-user-received])
       (is (= :ready @log-in-state))
       (is (not @log-in-failed?))

       (rf/dispatch [::events/update-log-in-state :invalid-state])
       (is (= :ready @log-in-state))
       (is (not @log-in-failed?))))))

(deftest test-sign-in
  (testing "sign in updates state and triggers the google sing in effect"
    (rf-test/run-test-sync
     (let [log-in-state (rf/subscribe [::subs/log-in-state])
           sign-in-calls (atom 0)]
       (rf/dispatch [::events/initialize-db])
       (rf/reg-fx :firebase/google-sign-in #(swap! sign-in-calls inc))
       (rf/dispatch [::events/sign-in])
       (is (= :logging-in @log-in-state))
       (is 1 @sign-in-calls)))))

(deftest test-sign-out
  (testing "sign out updates state and triggers the sign out effect"
    (rf-test/run-test-sync
     (let [log-in-state (rf/subscribe [::subs/log-in-state])
           sign-out-calls (atom 0)]
       (rf/reg-fx :firebase/sign-out #(swap! sign-out-calls inc))
       (rf/dispatch [::events/initialize-db])
       (rf/dispatch [::events/update-log-in-state :log-in])
       (rf/dispatch [::events/update-log-in-state :user-received])
       (is (= :logged-in @log-in-state))
       (rf/dispatch [::events/sign-out])
       (is (= :logging-out @log-in-state))
       (is 1 @sign-out-calls)))))

;; sort modal state

(deftest test-update-sort-modal-state
  (testing "Sort modal state is transitioned properly"
    (rf-test/run-test-sync
     (let [sort-modal-state (rf/subscribe [::subs/sort-modal-state])
           sort-modal-showing? (rf/subscribe [::subs/sort-modal-showing?])]
       (rf/dispatch [::events/initialize-db])
       (is (= :ready @sort-modal-state))
       (is (not @sort-modal-showing?))

       (rf/dispatch [::events/update-sort-modal-state :show])
       (is (= :showing @sort-modal-state))
       (is sort-modal-showing?)

       (rf/dispatch [::events/update-sort-modal-state :hide])
       (is (= :ready @sort-modal-state))
       (is (not @sort-modal-showing?))))))
