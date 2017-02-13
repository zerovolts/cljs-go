(ns go.core
  (:require [reagent.core :as reagent :refer [atom]]
            [go.ui :as ui]))

(enable-console-print!)
(println "Go!")

;;shuffle a moves list? (random thought)

(reagent/render-component
  [ui/draw-field]
  (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
