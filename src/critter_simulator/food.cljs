(ns ^:figwheel-always critter-simulator.food
    (:require
        [critter-simulator.point :as point]
        [critter-simulator.protocols.collidable :as collidable]))

(def radius 20)

(defrecord Food [position radius]
  collidable/Collidable
    (= [a b] (= a b))
    (position [self] (:position self))
    (closest-perimeter-distance [_ _] radius))

(defn make [env] (Food. [(/ (:width env) 2) (/ (:height env) 2)] radius))

(defn tap [text expr]
  (println text expr)
  expr)

(defn near? [f p]
   (> (+ 20 radius) (point/distance p (:position f))))