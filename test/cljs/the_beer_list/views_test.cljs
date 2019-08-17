(ns the-beer-list.views-test
  (:require [cljs.core :refer-macros [with-redefs]]
            [cljs.test :refer-macros [is testing]]
            [devcards.core :refer-macros [deftest]]
            [re-frame.core :as rf]
            [reagent.core :refer [atom]]
            [the-beer-list.events :as events]
            [the-beer-list.views :as views]
            [the-beer-list.subs :as subs]))

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
                         :cols 50
                         :max-length 200
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

(deftest test-text-input
  (testing "The text input component is correctly created"
    (with-redefs [value "Value"
                  rf/subscribe #(atom (case %
                                        [::subs/beer-modal-field-value :foo] value
                                        [::subs/beer-modal-field-error :foo] nil))
                  views/on-field-change identity
                  params {:id :foo
                          :label "Foo"
                          :placeholder "Foo placeholder"}]
      (is (= ((views/text-input params) params)
             (expected-text-input (merge params {:class nil
                                                 :value value
                                                 :error nil})))))))

(deftest test-text-input-with-error
  (testing "The text input component is created correctly with an error"
    (with-redefs [value "Value"
                  field-error "Field error"
                  rf/subscribe #(atom (case %
                                        [::subs/beer-modal-field-value :foo] value
                                        [::subs/beer-modal-field-error :foo] field-error))
                  views/on-field-change identity
                  params {:id :foo
                          :label "Foo"
                          :placeholder "Foo placeholder"}]
      (is (= ((views/text-input params) params)
             (expected-text-input (merge params {:class "is-danger"
                                                 :value value
                                                 :error (expected-field-error field-error)})))))))

(deftest test-textarea-input
  (testing "The textarea input component is created correctly"
    (with-redefs [value "Value"
                  rf/subscribe #(atom (case %
                                        [::subs/beer-modal-field-value :foo] value
                                        [::subs/beer-modal-field-error :foo] nil))
                  views/on-field-change identity
                  params {:id :foo
                          :label "Foo"
                          :placeholder "Foo placeholder"}]
      (is (= ((views/textarea-input params) params)
             (expected-textarea-input (merge params {:class nil
                                                     :value value
                                                     :error nil})))))))

(deftest test-textarea-input-with-error
  (testing "The textarea input component is created correctly with an error"
    (with-redefs [value "Value"
                  field-error "Field error"
                  rf/subscribe #(atom (case %
                                        [::subs/beer-modal-field-value :foo] value
                                        [::subs/beer-modal-field-error :foo] field-error))
                  views/on-field-change identity
                  params {:id :foo
                          :label "Foo"
                          :placeholder "Foo placeholder"}]
      (is (= ((views/textarea-input params) params)
             (expected-textarea-input (merge params {:class "is-danger"
                                                     :value value
                                                     :error (expected-field-error field-error)})))))))

(deftest test-select-input
  (testing "The select input input component is created correctly"
    (with-redefs [rf/subscribe #(atom (case [::subs/beer-modal-field-value :foo] nil))
                  views/on-field-change identity]
      (let [params {:id :foo
                    :label "Foo"
                    :options [{:value 1 :label "Option One"}
                              {:value 2 :label "Option Two"}]
                    :default-option 1}]
        (is (= ((views/select-input params) params)
               [:div.field
                [:label.label "Foo"]
                [:div.control
                 [:div.select
                  [:select {:on-change :foo
                            :value 1}
                   '([:option {:value 1} "Option One"]
                     [:option {:value 2} "Option Two"])]]]]))))))

;; beer modal

(declare call-count)
(declare beer)

(deftest test-close-beer-modal
  (testing "The close beer modal function dispatches the correct event"
    (with-redefs [call-count (atom 0)
                  rf/dispatch #(case %
                                 [::events/clear-and-hide-beer-modal]
                                 (swap! call-count inc))]
      (views/close-beer-modal)
      (is @call-count 1))))

(deftest test-save-beer
  (testing "The save beer function dispatches the correct event"
    (with-redefs [call-count (atom 0)
                  beer {:id 1 :name "Test"}
                  rf/dispatch #(case %
                                 [::events/try-save-beer beer]
                                 (swap! call-count inc))]
      (views/save-beer beer)
      (is @call-count 1))))

(deftest test-beer-form
  (testing "The beer form component is correctly created"
    (with-redefs [views/text-input :div
                  views/textarea-input :div
                  views/select-input :div]
      (is (= (views/beer-form)
             [:div {:role "form"}
              [:div {:id :name
                     :label "Beer Name"
                     :placeholder "A Delicious Brew"}]
              [:div {:id :brewery
                     :label "Brewery"
                     :placeholder "Your Favorite Brewery"}]
              [:div {:id :type
                     :label "Beer Type"
                     :placeholder "Ale? Lager? Gose?"}]
              [:div {:id :rating
                     :label "Rating"
                     :options views/rating-options
                     :default-option 3}]
              [:div {:id :comment
                     :label "Comment"
                     :placeholder "Provide more details here..."}]])))))

(deftest test-beer-modal-title-adding
  (testing "The correct title is displayed when adding"
    (is (= (views/beer-modal-title true)
           [:p.modal-card-title "Add a new beer!"]))))

(deftest test-beer-modal-title-updating
  (testing "The correct title is displayed when adding"
    (is (= (views/beer-modal-title false)
           [:p.modal-card-title "Update this beer"]))))

(deftest test-save-failed-message
  (testing "The save failed message is shown when a save fails"
    (is (= (views/save-failed-message true)
           [:article.message.is-danger
            [:div.message-body
             "Unable to save at this time, please try again later."]]))))

(deftest test-save-failed-message-hidden
  (testing "The save failed message is hidden if there is not a save failure"
    (is (nil? (views/save-failed-message false)))))

(defn expected-beer-modal
  [{:keys [class is-adding? save-failed?]}]
  [:div.modal {:class class}
   [:div.modal-background]
   [:div.modal-card
    [:header.modal-card-head
     [:beer-modal-title is-adding?]
     [:button.delete {:aria-label "close"
                      :on-click :close-beer-modal}]]
    [:section.modal-card-body
     [:save-failed save-failed?]
     [:beer-form]
    [:footer.modal-card-foot
     [:button.button.is-success {:on-click {}}
      "Save"]
     [:button.button {:on-click :close-beer-modal}
      "Cancel"]]]]])

(deftest test-beer-modal-adding
  (testing "Beer modal is correctly created when adding"
    (with-redefs [views/beer-form :beer-form
                  views/beer-modal-title :beer-modal-title
                  views/close-beer-modal :close-beer-modal
                  views/save-beer identity
                  views/save-failed-message :save-failed]
      (is (= (views/beer-modal-view {:beer {}
                                     :is-adding? true
                                     :showing? true
                                     :save-failed? false})
             (expected-beer-modal {:class "is-active"
                                   :is-adding? true
                                   :save-failed? false}))))))

(deftest test-beer-modal-editing
  (testing "Beer modal is correctly created when editing"
    (with-redefs [views/beer-form :beer-form
                  views/beer-modal-title :beer-modal-title
                  views/close-beer-modal :close-beer-modal
                  views/save-beer identity
                  views/save-failed-message :save-failed]
      (is (= (views/beer-modal-view {:beer {}
                                     :is-adding? false
                                     :showing? true
                                     :save-failed? false})
             (expected-beer-modal {:class "is-active"
                                   :is-adding? false
                                   :save-failed? false}))))))

(deftest test-beer-modal-save-failed
  (testing "Beer modal is correctly created when a save fails"
    (with-redefs [views/beer-form :beer-form
                  views/beer-modal-title :beer-modal-title
                  views/close-beer-modal :close-beer-modal
                  views/save-beer identity
                  views/save-failed-message :save-failed]
      (is (= (views/beer-modal-view {:beer {}
                                     :is-adding? true
                                     :showing? true
                                     :save-failed? true})
             (expected-beer-modal {:class "is-active"
                                   :is-adding? true
                                   :save-failed? true}))))))

(deftest test-beer-modal-hidden
  (testing "Beer modal is correctly created when a save fails"
    (with-redefs [views/beer-form :beer-form
                  views/beer-modal-title :beer-modal-title
                  views/close-beer-modal :close-beer-modal
                  views/save-beer identity
                  views/save-failed-message :save-failed]
      (is (= (views/beer-modal-view {:beer {}
                                     :is-adding? false
                                     :showing? false
                                     :save-failed? false})
             (expected-beer-modal {:class nil
                                   :is-adding? false
                                   :save-failed? false}))))))
;; (defn expected-save-failed-msg
;;   []
;;   [:article.message.is-danger
;;    [:div.message-body
;;     "Unable to save at this time, please try again later."]])
