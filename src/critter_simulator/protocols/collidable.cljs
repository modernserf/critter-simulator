(ns ^:figwheel-always critter-simulator.protocols.collidable
  (:require [critter-simulator.point :as point]))

(defprotocol Collidable
  "collision detection using bounding circles"
  (position
    [collidable]
    "center point of collidable")
  (closest-perimeter-distance
    [collidable target]
    "distance of collidable center to point on perimeter closest to target"))

; reference implementation of is-colliding, using other methods
(defn is-colliding? [a b]
  (and (not (eq? a b))
       (let [a-pos       (position a)
             b-pos       (position b)
             a-distance  (closest-perimeter-distance a b-pos)
             b-distance  (closest-perimeter-distance b a-pos)]
         (< (point/distance a-pos b-pos)
            (+ a-distance b-distance)))))
