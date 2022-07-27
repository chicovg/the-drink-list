(ns the-drink-list.uix-components.context
  (:require [uix.core :as uix]))

(def main-page (uix/create-context {:show-delete-modal! identity
                                    :show-drink-modal! identity}))
