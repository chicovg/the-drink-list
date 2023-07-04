(ns the-drink-list.devcards
  (:require [devcards.core :as dc :include-macros true]
            ["highlight.js" :as hljs]
            ["marked" :as marked]))

;; devcards uses global DevcardsSyntaxHighlighter and DevcardsMarked
;; ex. https://github.com/bhauman/devcards/blob/master/src/devcards/util/markdown.cljs#L28
;; therefore we need to define them
(js/goog.exportSymbol "DevcardsSyntaxHighlighter" hljs)
(js/goog.exportSymbol "DevcardsMarked" marked)

(defn ^:export init []
  (dc/start-devcard-ui!))

;; - app-cards
;;   DONE full app with state and everything
;; - page-cards
;;   - main
;;   - login-panel
;;   - favorites
;; - content-cards
;;   - DONE drink-list
;;   - DONE navbar
;;   - DONE options-nav
;; - modal-cards
;;   - DONE delete-modal
;;   - DONE drink-modal
;; - input-cards
;;   - DONE autocomplete
;;   - DONE select-tags
;;   - DONE slider-input
;;   - DONE text-input
;;   - DONE textarea-input
;;
;;
;;
