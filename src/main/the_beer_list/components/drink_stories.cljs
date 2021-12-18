(ns the-beer-list.components.drink-stories
  (:require [the-beer-list.components.drink :as drink]
            [the-beer-list.types.drink :as drink-type]
            [reagent.core :as r]))

(def ^:export default
  #js {:title     "Drink Card Component"
       :component (r/reactify-component drink/card)})

(def example-comment
  "Lorem ipsum dolor sit amet, consectetur adipiscing elit. In lacinia ac eros sit amet pulvinar. Proin dignissim posuere urna ac vehicula. Nam ullamcorper eros in tortor euismod feugiat. Nulla quis fringilla dolor. Curabitur est metus, finibus aliquam diam vel, sagittis rhoncus tellus. Donec ac vulputate mi. Vestibulum vel enim vel orci egestas mollis et ut enim. Suspendisse euismod, mauris quis lacinia commodo, sapien diam eleifend nunc, in vulputate quam ligula eu ex. Maecenas mollis posuere nisl eu elementum. Vestibulum efficitur a felis eu molestie. Duis quis lectus sed erat pellentesque porttitor sit amet ut ipsum. Suspendisse at scelerisque ex, at malesuada nibh. Proin gravida, lacus in dapibus dapibus, mauris neque imperdiet ligula, eu auctor tellus turpis accumsan turpis. Nunc rhoncus tortor vitae velit ornare tristique. Nunc mollis, metus imperdiet tempus varius, dolor augue sagittis felis, et mattis ex ligula vitae nibh. Donec nisi nisi, aliquam eu aliquet eget, lobortis sit amet arcu.")

(defn ^:export Default []
  (r/as-element [drink/card {:drink (assoc
                                     (drink-type/gen-drink)
                                     :comment       example-comment
                                     :created       (js/Date. (- (js/Date.) 0))
                                     :notes         (drink-type/gen-notes 10))}]))

(defn ^:export NoComment []
  (r/as-element [drink/card {:drink (assoc
                                     (drink-type/gen-drink)
                                     :comment       nil
                                     :notes         (drink-type/gen-notes 10))}]))

(defn ^:export ShortComment []
  (r/as-element [drink/card {:drink (assoc
                                     (drink-type/gen-drink)
                                     :comment       "Not much to say"
                                     :notes         (drink-type/gen-notes 10))}]))

(defn ^:export NoNotes []
  (r/as-element [drink/card {:drink (assoc
                                     (drink-type/gen-drink)
                                     :comment       example-comment
                                     :notes         nil)}]))
