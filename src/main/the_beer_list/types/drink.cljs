(ns the-beer-list.types.drink
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test.check.generators]
            [the-beer-list.types.beer-flavors :as beer-flavors]))

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

(s/def ::drink
  (s/keys :req-un [::name
                   ::maker
                   ::type
                   ::style
                   ::appearance
                   ::smell
                   ::taste]
          :opt-un [::id
                   ::notes
                   ::comment]))

(defn round
  [n]
  (as-> n $
    (* 10 $)
    (.round js/Math $)
    (/ $ 10)))

(defn gen-drink []
  (-> (gen/generate (s/gen ::drink))
      (update :appearance round)
      (update :smell round)
      (update :taste round)))
