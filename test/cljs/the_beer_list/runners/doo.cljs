(ns runners.doo
  (:require [doo.runner :refer-macros [doo-tests]]
            [the-drink-list.core-test]
            [the-drink-list.views-test]))

(doo-tests 'the-drink-list.core-test
           'the-drink-list.views-test)
