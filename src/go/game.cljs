(ns go.game
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.set :as cset]))

(defonce game-state
  (atom {:stones []
         :dead-stones []
         :turn-color :black
         :board-size 19
         :position {:x 0 :y 0}}))

(defn create-stone
  "Returns a hashmap representing a stone."
  [x y color]
  {:x x :y y :color color})

(defn random-stone
  "Creates a stone of random color and position."
  []
  (create-stone (inc (rand-int (@game-state :board-size)))
                (inc (rand-int (@game-state :board-size)))
                (rand-nth [:white :black])))

;; Return :invalid if out of board range.
(defn get-stone
  "Returns the stone at the given coordinates, or :empty."
  [x y]
  (if (and (> x 0)
           (> y 0)
           (<= x (@game-state :board-size))
           (<= y (@game-state :board-size)))
    (let [stone (first (filter #(= [(%1 :x) (%1 :y)]
                               [x y])
                        (@game-state :stones)))]
      (if (nil? stone)
          :empty
          stone))
    :invalid))

(defn get-empty
  []
  (for [x (map inc (range 19))
        y (map inc (range 19))
        :let [positions (create-stone x y :empty)]
        :when (= :empty (get-stone (positions :x) (positions :y)))]
    positions))

(defn place-stone
  "Adds the given stone to the game board."
  [stone]
  (if (= :empty (get-stone (stone :x)
                           (stone :y)))
    (swap! game-state
           assoc-in
           [:stones (count (@game-state :stones))]
           stone)))

(defn adj-stones
  "Returns a vector of same-color stones adjacent to the given stone."
  [stone]
  (let [spaces (if (not (empty? stone))
                 [(get-stone (dec (stone :x)) (stone :y))
                  (get-stone (inc (stone :x)) (stone :y))
                  (get-stone (stone :x) (dec (stone :y)))
                  (get-stone (stone :x) (inc (stone :y)))])
       stones (filter some? spaces)]
    (filterv #(= (%1 :color) (stone :color)) stones)))

(defn liberties
  [stone]
  (count (filter #(= % :empty)
                 [(get-stone (dec (stone :x)) (stone :y))
                  (get-stone (inc (stone :x)) (stone :y))
                  (get-stone (stone :x) (dec (stone :y)))
                  (get-stone (stone :x) (inc (stone :y)))])))

(defn make-group
  "Similar to adj-stones, but takes a collection of stones, and returns the union of the input and output."
  [stones]
  (set (apply concat (conj (map adj-stones stones) stones))))

(defn get-groups
  "Takes a collection of stones and returns a vector of all connected stones."
  [stones]
  (let [group (make-group stones)]
    (if (= group (set stones))
        (vec group)
        (recur group))))

(defn get-group
  "Same as get-groups, but takes a single stone. It is more natural to use."
  [stone]
  (get-groups [stone]))

(defn alternate-turn
  "Alternate which color is in the :turn-color field of game-state."
  []
  (swap! game-state
         assoc
         :turn-color
         (case (@game-state :turn-color)
               :black :white
               :white :black)))

(defn ai-play
  []
  (let [stone (assoc (rand-nth (get-empty)) :color (@game-state :turn-color))]
    (and (place-stone stone)
         (alternate-turn))))

(defn play
  "This should be the only function that needs to be called from outside of this namespace."
  [x y]
  (let [stone (create-stone x y (@game-state :turn-color))]
    ;; short-circuit and
    (and (> (liberties stone) 0)
         (place-stone stone)
         (alternate-turn)
         (js/setTimeout #(ai-play) 500))))

(defn score
  [])
