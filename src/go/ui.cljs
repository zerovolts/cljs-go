(ns go.ui
  (:require [go.game :as game]))

(defn draw-board-back
  []
  [:rect
   {:x 0
    :y 0
    :width 480
    :height 480
    :fill "#FFA02B"}])

(defn draw-horizontal-line
  [y]
  [:line
   {:x1 23
    :y1 (* y 24)
    :x2 457
    :y2 (* y 24)
    :stroke "#222"
    :stroke-width 2}])

(defn draw-vertical-line
  [x]
  [:line
   {:x1 (* x 24)
    :y1 23
    :x2 (* x 24)
    :y2 457
    :stroke "#222"
    :stroke-width 2}])

(defn draw-marker
  [x y]
  [:circle
   {:cx (* x 24)
    :cy (* y 24)
    :r 4
    :fill "#222"}])

(defn draw-stone
  [x y color]
  [:g [:circle
   {:cx (* x 24)
    :cy (* y 24)
    :r 12
    :fill (case color
            :white "#EEE"
            :black "#222")}]
   [:circle
    {:cx (+ (* x 24) 4)
     :cy (- (* y 24) 4)
     :r 3
     :fill (case color
             :white "#FFF"
             :black "#444")}]])

(defn click-box
  [x y]
  [:rect
   {:x (- (* x 24) 12)
    :y (- (* y 24) 12)
    :width 24
    :height 24
    :fill-opacity 0
    :on-mouse-down #(go.game/play x y)
    :on-mouse-over #(swap! go.game/game-state
                           assoc
                           :position
                           {:x x :y y})}])

(defn draw-board
  []
  [(draw-board-back)
   (map #(draw-horizontal-line (inc %1))
        (range 19))
   (map #(draw-vertical-line (inc %1))
        (range 19))
   (for [x [4 10 16]
         y [4 10 16]]
     (draw-marker x y))
   (map #(draw-stone (%1 :x)
                     (%1 :y)
                     (%1 :color))
        (@game/game-state :stones))
   (let [positions (map inc
                        (range (@go.game/game-state
                                :board-size)))]
     (for [x positions
           y positions]
       (click-box x y)))])

(defn draw-field
  []
  [:center
   [:h1 "囲碁"]
   (into
    [:svg
     {:width 480
      :height 480}]
    (draw-board))

   [:br]
   [:button {:on-click #(game/place-stone (game/random-stone))}
            "Random!"]
   [:p "Position: "
       ((@game/game-state :position) :x)
       ", "
       ((@game/game-state :position) :y)]
   [:p "Turn: "
       (count (@game/game-state :stones))]
   [:p "Black Stones: "
       (count (filter #(= (:color %1) :black)
                      (@game/game-state :stones)))]
   [:p "White Stones: "
       (count (filter #(= (:color %1) :white)
                      (@game/game-state :stones)))]])
