(ns the-beer-list.core-test
  (:require [cljs.test :refer-macros [deftest is]]
            [re-frame.core :as rf]
            [day8.re-frame.test :as rf-test]
            [the-beer-list.events :as events]
            [the-beer-list.subs :as subs]))

(deftest test-init
  (rf-test/run-test-sync
   (rf/dispatch [::events/initialize-db])
   (let [active-panel (rf/subscribe [::subs/active-panel])
         beers (rf/subscribe [::subs/beers])
         beer-modal (rf/subscribe [::subs/beer-modal])
         delete-confirm-modal (rf/subscribe [::subs/delete-confirm-modal])]
     (is (nil? @active-panel))
     ;; TODO start empty
     (is (not (= [] @beers)))
     (is (= {:beer nil
             :operation nil
             :show false}
            @beer-modal))
     (is (= {:id nil
             :show false}
            @delete-confirm-modal)))))

(deftest test-set-active-panel
  (rf-test/run-test-sync
   (rf/dispatch [::events/initialize-db])
   (let [active-panel (rf/subscribe [::subs/active-panel])]
     (rf/dispatch [::events/set-active-panel :foo])
     (is (= :foo @active-panel)))))

(deftest test-show-add-beer-modal
  (rf-test/run-test-sync
   (rf/dispatch [::events/initialize-db])
   (let [beer-modal (rf/subscribe [::subs/beer-modal])]
     (rf/dispatch [::events/show-add-beer-modal])
     (is (= {:beer {:name ""
                    :brewery ""
                    :type ""
                    :rating 3
                    :comment ""}
             :operation :add
             :show true}
            @beer-modal)))))

(deftest test-show-edit-beer-modal
  (rf-test/run-test-sync
   (rf/dispatch [::events/initialize-db])
   (let [beer-modal (rf/subscribe [::subs/beer-modal])
         beer-to-edit {:name "Test"
                       :brewery "Test"
                       :type "Test"
                       :rating 1
                       :comment "Test"}]
     (rf/dispatch [::events/show-edit-beer-modal beer-to-edit])
     (is (= {:beer beer-to-edit
             :operation :edit
             :show true}
            @beer-modal)))))

(deftest test-hide-beer-modal
  (rf-test/run-test-sync
   (rf/dispatch [::events/initialize-db])
   (let [beer-modal (rf/subscribe [::subs/beer-modal])]
     (rf/dispatch [::events/show-add-beer-modal])
     (rf/dispatch [::events/hide-beer-modal])
     (is (= {:beer nil
             :operation nil
             :show false}
            @beer-modal)))))

(deftest test-save-beer
  (rf-test/run-test-sync
   (rf/dispatch [::events/initialize-db])
   (let [beers (rf/subscribe [::subs/beers])
         name "Test name"
         brewery "Test brewery"
         type "Test type"
         rating 2
         comment "Test comment"
         ]
     (rf/dispatch [::events/save-beer {:name name
                                       :brewery brewery
                                       :type type
                                       :rating rating
                                       :comment comment}])
     (is (some #(and
                 (= (:name %) name)
                 (= (:brewery %) brewery)
                 (= (:type %) type)
                 (= (:rating %) rating)
                 (= (:comment %) comment)) @beers)))))

(deftest test-show-delete-confirm-modal
  (rf-test/run-test-sync
   (rf/dispatch [::events/initialize-db])
   (let [delete-confirm-modal (rf/subscribe [::subs/delete-confirm-modal])]
     (rf/dispatch [::events/show-delete-confirm-modal 1])
     (is (= {:id 1
             :show true}
            @delete-confirm-modal)))))

(deftest test-hide-delete-confirm-modal
  (rf-test/run-test-sync
   (rf/dispatch [::events/initialize-db])
   (rf/dispatch [::events/show-delete-confirm-modal 1])
   (let [delete-confirm-modal (rf/subscribe [::subs/delete-confirm-modal])]
     (rf/dispatch [::events/hide-delete-confirm-modal])
     (is (= {:id nil
             :show false}
            @delete-confirm-modal)))))

(deftest test-delete-beer
  (rf-test/run-test-sync
   (rf/dispatch [::events/initialize-db])
   (let [name "Test name"
         brewery "Test brewery"
         type "Test type"
         rating 2
         comment "Test comment"]
     (rf/dispatch [::events/save-beer {:name name
                                       :brewery brewery
                                       :type type
                                       :rating rating
                                       :comment comment}])
     (let [beers (rf/subscribe [::subs/beers])
           {:keys [id]} (first (filter #(= (:name %) name) @beers))]
       (rf/dispatch [::events/delete-beer id])
       (is (not (some #(= (:name %) name) @beers)))))))

(deftest test-set-beer-list-filter
  (rf-test/run-test-sync
   (rf/dispatch [::events/initialize-db])
   (let [beers (rf/subscribe [::subs/beers])
         name "Test name"
         brewery "Test brewery"
         type "Test type"
         rating 2
         comment "Test comment"]
     (rf/dispatch [::events/save-beer {:name name
                                       :brewery brewery
                                       :type type
                                       :rating rating
                                       :comment comment}])
     (rf/dispatch [::events/set-beer-list-filter name])
     (is (= 1 (count @beers))))))
