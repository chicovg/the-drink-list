(ns the-drink-list.views-test
  (:require [cljs.core :refer-macros [with-redefs]]
            [cljs.test :refer-macros [is testing]]
            [devcards.core :refer-macros [deftest]]
            [re-frame.core :as rf]
            [reagent.core :refer [atom]]
            [the-drink-list.events :as events]
            [the-drink-list.views :as views]
            [the-drink-list.paths :as paths]
            [the-drink-list.subs :as subs]))

;; form fields

(defn expected-text-input
  [{:keys [label class id placeholder value error]}]
  [:div.field
   [:label.label label]
   [:div.control
    [:input.input {:type "text"
                   :class class
                   :placeholder placeholder
                   :on-change id
                   :value value}]
    error]])

(defn expected-textarea-input
  [{:keys [label class id placeholder value error]}]
  [:div.field
   [:label.label label]
   [:div.control
    [:textarea.textarea {:class class
                         :rows 4
                         :cols 200
                         :max-length 800
                         :placeholder placeholder
                         :on-change id
                         :value value}]
    error]])

(defn expected-field-error
  [field-error]
  [:p.help.is-danger field-error])

(declare value)
(declare params)
(declare field-error)

;; (deftest test-text-input
;;   (testing "The text input component is correctly created"
;;     (with-redefs [value "Value"
;;                   rf/subscribe #(atom (case %
;;                                         [::subs/beer-form-field-value :foo] value
;;                                         [::subs/beer-form-field-error :foo] nil))
;;                   views/on-field-change identity
;;                   params {:id :foo
;;                           :label "Foo"
;;                           :placeholder "Foo placeholder"}]
;;       (is (= ((views/text-input params) params)
;;              (expected-text-input (merge params {:class nil
;;                                                  :value value
;;                                                  :error nil})))))))

;; (deftest test-text-input-with-error
;;   (testing "The text input component is created correctly with an error"
;;     (with-redefs [value "Value"
;;                   field-error "Field error"
;;                   rf/subscribe #(atom (case %
;;                                         [::subs/beer-form-field-value :foo] value
;;                                         [::subs/beer-form-field-error :foo] field-error))
;;                   views/on-field-change identity
;;                   params {:id :foo
;;                           :label "Foo"
;;                           :placeholder "Foo placeholder"}]
;;       (is (= ((views/text-input params) params)
;;              (expected-text-input (merge params {:class "is-danger"
;;                                                  :value value
;;                                                  :error (expected-field-error field-error)})))))))

;; (deftest test-textarea-input
;;   (testing "The textarea input component is created correctly"
;;     (with-redefs [value "Value"
;;                   rf/subscribe #(atom (case %
;;                                         [::subs/beer-form-field-value :foo] value
;;                                         [::subs/beer-form-field-error :foo] nil))
;;                   views/on-field-change identity
;;                   params {:id :foo
;;                           :label "Foo"
;;                           :placeholder "Foo placeholder"}]
;;       (is (= ((views/textarea-input params) params)
;;              (expected-textarea-input (merge params {:class nil
;;                                                      :value value
;;                                                      :error nil})))))))

;; (deftest test-textarea-input-with-error
;;   (testing "The textarea input component is created correctly with an error"
;;     (with-redefs [value "Value"
;;                   field-error "Field error"
;;                   rf/subscribe #(atom (case %
;;                                         [::subs/beer-form-field-value :foo] value
;;                                         [::subs/beer-form-field-error :foo] field-error))
;;                   views/on-field-change identity
;;                   params {:id :foo
;;                           :label "Foo"
;;                           :placeholder "Foo placeholder"}]
;;       (is (= ((views/textarea-input params) params)
;;              (expected-textarea-input (merge params {:class "is-danger"
;;                                                      :value value
;;                                                      :error (expected-field-error field-error)})))))))

;; (deftest test-select-input
;;   (testing "The select input input component is created correctly"
;;     (with-redefs [rf/subscribe #(atom (case [::subs/beer-form-field-value :foo] nil))
;;                   views/on-field-change identity]
;;       (let [params {:id :foo
;;                     :label "Foo"
;;                     :options [{:value 1 :label "Option One"}
;;                               {:value 2 :label "Option Two"}]
;;                     :default-option 1}]
;;         (is (= ((views/select-input params) params)
;;                [:div.field
;;                 [:label.label "Foo"]
;;                 [:div.control
;;                  [:div.select
;;                   [:select {:on-change :foo
;;                             :value 1}
;;                    '([:option {:value 1} "Option One"]
;;                      [:option {:value 2} "Option Two"])]]]]))))))

;; beer modal

(declare call-count)
(declare beer)

(deftest test-save-beer
  (testing "The save beer function dispatches the correct event"
    (with-redefs [call-count (atom 0)
                  beer {:id 1 :name "Test"}
                  rf/dispatch #(case %
                                 [::events/try-save-beer beer]
                                 (swap! call-count inc))]
      ((views/save-beer beer))
      (is (= 1 @call-count)))))

(deftest test-close-save-success-message
  (testing "The close save success message function dispatches the correct event"
    (with-redefs [call-count (atom 0)
                  rf/dispatch #(case %
                                 [::events/update-beer-form-state :hide]
                                 (swap! call-count inc))]
      (views/close-save-success-message)
      (is (= 1 @call-count)))))

(declare id)

(deftest test-show-delete-confirm-modal
  (testing "The show delete confirm modal function dispatches the correct event"
    (with-redefs [id "one"
                  call-count (atom 0)
                  rf/dispatch #(case %
                                 [::events/show-delete-confirm-modal id]
                                 (swap! call-count inc))]
      ((views/show-delete-confirm-modal {:id id}))
      (is (= 1 @call-count)))))

(defn expected-beer-form
  [delete-button]
  [:div {:role "form"}
   [:div.tile.is-ancestor
    [:div.tile.is-parent
     [:div.tile.is-child
      [:div {:id :name
             :label "Beer Name"
             :placeholder "A Delicious Brew"}]]
     [:div.tile.is-child
      [:div {:id :brewery
             :label "Brewery"
             :placeholder "Your Favorite Brewery"}]]]]
   [:div.tile.is-ancestor
    [:div.tile.is-parent
     [:div.tile.is-child
      [:div {:id :type
             :label "Beer Type"
             :placeholder "Ale? Lager? Gose?"}]]
     [:div.tile.is-child
      [:div {:id :rating
             :label "Rating"
             :options views/rating-options
             :default-option 3}]]]]
   [:div.tile.is-ancestor
    [:div.tile.is-parent.is-12
     [:div {:id :comment
            :label "Comment"
            :placeholder "Provide more details here..."}]]]

   [:div.tile.is-ancestor
    [:div.tile.is-parent
     [:button.button.is-primary {:on-click beer} "Save"]
     delete-button
     [:a.button {:href "#/"} "Cancel"]]]])

(deftest test-beer-form
  (testing "The beer form component is correctly created"
    (with-redefs [views/text-input :div
                  views/textarea-input :div
                  views/select-input :div
                  views/save-beer identity
                  beer {:id 1}]
      (is (= (views/beer-form beer false)
             (expected-beer-form nil))))))

(deftest test-beer-form-with-delete-button
  (testing "The beer form component is correctly created"
    (with-redefs [views/text-input :div
                  views/textarea-input :div
                  views/select-input :div
                  views/save-beer identity
                  views/show-delete-confirm-modal identity
                  beer {:id 1}]
      (is (= (views/beer-form beer true)
             (expected-beer-form
              [:button.button.is-danger {:on-click beer} "Delete"]))))))

(deftest test-beer-panel-title-adding
  (testing "The correct title is displayed when adding"
    (is (= (views/beer-panel-title true)
           [:h1.title "Add a new beer!"]))))

(deftest test-beer-panel-title-updating
  (testing "The correct title is displayed when adding"
    (is (= (views/beer-panel-title false)
           [:h1.title "Update this beer"]))))

(deftest test-save-failed-message
  (testing "The save failed message is shown when a save fails"
    (is (= (views/save-failed-message true)
           [:article.message.is-danger
            [:div.message-body
             "Unable to save at this time, please try again later."]]))))

(deftest test-save-failed-message-hidden
  (testing "The save failed message is hidden if there is not a save failure"
    (is (nil? (views/save-failed-message false)))))

(deftest test-save-success-message
  (testing "The save success message is shown when a save succeeds"
    (is (= (views/save-success-message true)
           [:article.message.is-success
            [:div.message-header
             [:p "Saved Successfully"]
             [:button.delete {:aria-label "delete"
                              :on-click views/close-save-success-message}]]
            [:div.message-body
             [:a.button {:href paths/home-path
                         :on-click views/close-save-success-message}
              "Go Home"]]]))))

(deftest test-panel-view-adding
  (testing "The beer panel view is created correctly while adding"
    (with-redefs [views/save-in-progress :save-in-progress
                  views/save-failed-message :save-failed-message
                  views/save-success-message :save-success-message
                  views/beer-panel-title :beer-panel-title
                  views/beer-form :beer-form
                  beer {:id 1}]
      (is (= (views/beer-panel-view {:beer beer
                                     :is-adding? true
                                     :is-saving? false
                                     :save-failed? false
                                     :save-succeeded? false})
             [:div.container
              [:save-in-progress false]
              [:save-failed-message false]
              [:save-success-message false]
              [:beer-panel-title true]
              [:beer-form beer false]])))))

(deftest test-panel-view-editing
  (testing "The beer panel view is created correctly while editing"
    (with-redefs [views/save-in-progress :save-in-progress
                  views/save-failed-message :save-failed-message
                  views/save-success-message :save-success-message
                  views/beer-panel-title :beer-panel-title
                  views/beer-form :beer-form
                  beer {:id 1}]
      (is (= (views/beer-panel-view {:beer beer
                                     :is-adding? false
                                     :is-saving? false
                                     :save-failed? false
                                     :save-succeeded? false})
             [:div.container
              [:save-in-progress false]
              [:save-failed-message false]
              [:save-success-message false]
              [:beer-panel-title false]
              [:beer-form beer true]])))))

(deftest test-panel-view-saving
  (testing "The beer panel view is created correctly while saving"
    (with-redefs [views/save-in-progress :save-in-progress
                  views/save-failed-message :save-failed-message
                  views/save-success-message :save-success-message
                  views/beer-panel-title :beer-panel-title
                  views/beer-form :beer-form
                  beer {:id 1}]
      (is (= (views/beer-panel-view {:beer beer
                                     :is-adding? false
                                     :is-saving? true
                                     :save-failed? false
                                     :save-succeeded? false})
             [:div.container
              [:save-in-progress true]
              [:save-failed-message false]
              [:save-success-message false]
              [:beer-panel-title false]
              [:beer-form beer true]])))))

(deftest test-panel-view-save-failed
  (testing "the beer panel view is created correctly when save failed"
    (with-redefs [views/save-in-progress :save-in-progress
                  views/save-failed-message :save-failed-message
                  views/save-success-message :save-success-message
                  views/beer-panel-title :beer-panel-title
                  views/beer-form :beer-form
                  beer {:id 1}]
      (is (= (views/beer-panel-view {:beer beer
                                     :is-adding? false
                                     :is-saving? false
                                     :save-failed? true
                                     :save-succeeded? false})
             [:div.container
              [:save-in-progress false]
              [:save-failed-message true]
              [:save-success-message false]
              [:beer-panel-title false]
              [:beer-form beer true]])))))

(deftest test-panel-view-save-succeeded
  (testing "the beer panel view is created correctly when save succeeded"
    (with-redefs [views/save-in-progress :save-in-progress
                  views/save-failed-message :save-failed-message
                  views/save-success-message :save-success-message
                  views/beer-panel-title :beer-panel-title
                  views/beer-form :beer-form
                  beer {:id 1}]
      (is (= (views/beer-panel-view {:beer beer
                                     :is-adding? false
                                     :is-saving? false
                                     :save-failed? false
                                     :save-succeeded? true})
             [:div.container
              [:save-in-progress false]
              [:save-failed-message false]
              [:save-success-message true]
              [:beer-panel-title false]
              [:beer-form beer true]])))))

(deftest test-close-delete-confirm-modal
  (testing "The close delete confirm modal function dispatches the correct event"
    (with-redefs [call-count (atom 0)
                  rf/dispatch #(case %
                                 [::events/update-delete-confirm-state :hide]
                                 (swap! call-count inc))]
      (views/close-delete-confirm-modal)
      (is (= 1 @call-count)))))

(deftest test-delete-beer
  (testing "The delete beer function dispatches the correct event"
    (with-redefs [call-count (atom 0)
                  id 1
                  rf/dispatch #(case %
                                 [::events/delete-beer id]
                                 (swap! call-count inc))]
      ((views/delete-beer id))
      (is (= 1 @call-count)))))

(deftest test-delete-failed-message
  (testing "The delete failed message is created correctly"
    (is (= (views/delete-failed-message true)
           [:article.message.is-danger
            [:div.message-body
             "Unable to delete at this time, please try again later."]]))))

(deftest test-delete-failed-message-hidden
  (testing "The delete failed message can be hidden"
    (is (nil? (views/delete-failed-message false)))))

(defn expected-delete-confirm-modal
  [class delete-failed? on-click]
  [:div.modal {:class class}
   [:div.modal-background]
   [:div.modal-card
    [:header.modal-card-head
     [:p.modal-card-title "Please confirm"]
     [:button.delete {:aria.label "close"
                      :on-click views/close-delete-confirm-modal}]]
    [:section.modal-card-body
     [:delete-failed-message delete-failed?]
     [:p "Are you sure that you want to delete?"]]
    [:footer.modal-card-foot
     [:a.button.is-danger {:href paths/home-path
                           :on-click on-click} "Delete"]
     [:button.button {:on-click views/close-delete-confirm-modal} "Cancel"]]]])

(deftest test-delete-confirm-modal
  (testing "The delete confirm modal is correctly created"
    (with-redefs [views/delete-failed-message :delete-failed-message
                  views/delete-beer identity]
      (is (= (views/delete-confirm-modal-view {:modal-showing? true
                                               :id 1
                                               :delete-failed? false})
             (expected-delete-confirm-modal "is-active"
                                            false
                                            1))))))

(deftest test-delete-confirm-modal-with-failure
  (testing "The delete confirm modal is correctly created with a failure"
    (with-redefs [views/delete-failed-message :delete-failed-message
                  views/delete-beer identity]
      (is (= (views/delete-confirm-modal-view {:modal-showing? true
                                               :id 1
                                               :delete-failed? true})
             (expected-delete-confirm-modal "is-active"
                                            true
                                            1))))))

(deftest test-delete-confirm-modal-not-shown
  (testing "The delete confirm modal is correctly created with a failure"
    (with-redefs [views/delete-failed-message :delete-failed-message
                  views/delete-beer identity]
      (is (= (views/delete-confirm-modal-view {:modal-showing? false
                                               :id 1
                                               :delete-failed? false})
             (expected-delete-confirm-modal nil
                                            false
                                            1))))))

;; loading modal

;; sort modal

;; toolbar

;; beer list
