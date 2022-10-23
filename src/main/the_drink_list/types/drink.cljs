(ns the-drink-list.types.drink
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test.check.generators]
            [the-drink-list.types.beer-flavors :as beer-flavors]
            [clojure.string :as str]))

(def types #{"Beer" "Cider" "Mead" "Other" "Wine"})

(s/def ::id string?)
(s/def ::name string?)

(def fake-brewers #{"Amazing Brewery"
                    "Blistering Sourness Brewers"
                    "Beer Makers 123"
                    "Happy Valley Brewing Company"
                    "Great Booze Brewing"})
(s/def ::maker
  (s/with-gen string? #(s/gen fake-brewers)))
(s/def ::type types)

(def example-styles #{"Gose"
                      "Hefeweisen"
                      "IPA"
                      "Lager"
                      "Pale Ale"
                      "Porter"
                      "Stout"})
(s/def ::style
  (s/with-gen string? #(s/gen example-styles)))

(s/def ::rating (s/double-in :min 1 :max 5))
(s/def ::appearance ::rating)
(s/def ::smell ::rating)
(s/def ::taste ::rating)

(s/def ::note
  (s/with-gen string? #(s/gen beer-flavors/flavors)))

(s/def ::notes (s/nilable (s/coll-of ::note)))
(s/def ::comment (s/nilable string?))
(s/def ::created inst?)

(s/def ::drink
  (s/keys :req-un [::name
                   ::maker
                   ::type
                   ::style
                   ::appearance
                   ::smell
                   ::taste
                   ::created]
          :opt-un [::id
                   ::notes
                   ::comment]))

(def default-values
  {:type       "Beer"
   :appearance 3
   :smell      3
   :taste      3})

(defn round
  [n]
  (as-> n $
    (* 10 $)
    (.round js/Math $)
    (/ $ 10)))

(defn is-valid?
  [drink]
  (s/valid? ::drink drink))

(defn calculate-overall
  [{:keys [appearance smell taste]}]
  (/ (+ appearance smell taste taste taste)
     5))

(defn set-overall
  [drink]
  (assoc drink :overall (round (calculate-overall drink))))

(defn- trim-non-nil
  [s]
  (and s (str/trim s)))

(defn trim-fields
  [drink]
  (-> drink
      (update :name trim-non-nil)
      (update :maker trim-non-nil)
      (update :type trim-non-nil)
      (update :style trim-non-nil)
      (update :comment trim-non-nil)
      (update :notes #(when % (mapv trim-non-nil %)))))

(defn gen-drink []
  (-> (gen/generate (s/gen ::drink))
      (assoc :id (gen/generate (s/gen ::id)))
      set-overall
      (update :appearance round)
      (update :smell round)
      (update :taste round)
      (update :notes distinct)))

(defn gen-notes [count]
  (take count (distinct (gen/sample (s/gen ::note)))))

(defn gen-drinks [count]
  (repeatedly count gen-drink))
