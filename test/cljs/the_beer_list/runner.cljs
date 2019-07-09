(ns the-beer-list.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [the-beer-list.core-test]))

(doo-tests 'the-beer-list.core-test)
