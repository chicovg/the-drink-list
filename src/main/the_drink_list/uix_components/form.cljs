(ns the-drink-list.uix-components.form
  (:require
   [clojure.string :as s]
   [reagent.core :as r]
   ["@yaireo/tagify/dist/react.tagify" :default Tags]))

(defn- event->value
  [e]
  (-> e .-target .-value))

(defn validate
  [required label value]
  (when (and required (empty? value))
    (str label " is required")))

(defn input
  [type element _]
  (r/with-let [dirty? (r/atom false)
               error  (r/atom nil)]
    (fn [{:keys [id label on-change required] :as props}]
      [:div.field
       [:label.label {:for id} label]
       [:div.control
        [element (assoc props
                        :type type
                        :on-change #(let [val (event->value %)]
                                      (reset! dirty? true)
                                      (reset! error (validate required label val))
                                      (on-change val)))]]
       (when (and @dirty? @error)
         [:p.help.is-danger @error])])))

(def text-input
  (partial input "text" :input.input))

(def textarea-input
  (partial input nil :textarea.textarea))

(defn slider-input
  [{:keys [id label on-change value] :as props}]
  [:div.field
   [:label.label {:for id} label]
   [:input.slider.has-output.is-primary.is-full-width
    (assoc props
           :id        id
           :on-change #(on-change (some-> %
                                          event->value
                                          js/Number))
           :type      "range"
           :value     (str value))]
   [:output {:for id} value]])

(defn select-input
  [_]
  (r/with-let [dirty? (r/atom false)
               error  (r/atom nil)]
    (fn [{:keys [id label on-change options required value] :as props}]
      [:div.field
       [:label.label {:for id} label]
       [:div.control
        [:div.select
         [:select (cond-> props
                    true
                    (assoc :on-change #(let [val (event->value %)]
                                         (reset! dirty? true)
                                         (reset! error (validate required label val))
                                         (on-change val)))
                    (nil? value)
                    (dissoc :value))
          (for [{:keys [label value]} options]
            ^{:key value}
            [:option {:value value} label])]]]
       (when (and @dirty? @error)
         [:p.help.is-danger @error])])))

(defn- blank->nil
  [s]
  (when (not-empty s)
    s))

(defn- event->tag-values
  [e]
  (some->> e
           .-detail
           .-value
           blank->nil
           js/JSON.parse
           js->clj
           (mapv (fn [o]
                   (get o "value")))))

(defn select-tags-input
  [{:keys [id label on-change options placeholder value]}]
  [:div.field
   [:label.label {:for id} label]
   [:div.control
    [:> Tags (cond-> {:id          id
                      :on-change   #(on-change (event->tag-values %))
                      :placeholder placeholder
                      :settings    {:add-tags-on-blur true
                                    :dropdown         {:enabled 0}
                                    :whitelist        options}}
               (not-empty value)
               (assoc :value (s/join "," value)))]]])
