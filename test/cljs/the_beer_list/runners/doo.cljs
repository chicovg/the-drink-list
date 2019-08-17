(ns runners.doo
  (:require [doo.runner :refer-macros [doo-tests]]
            [the-beer-list.core-test]
            [the-beer-list.views-test]))

(doo-tests 'the-beer-list.core-test
           'the-beer-list.views-test)
