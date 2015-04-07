(ns ^:figwheel-always critter-simulator.behavior.defaults
    (:require
        [critter-simulator.behavior.core :as behavior]))

; TODO: handle behaviors that change environment e.g. (eating, pooping)
(def critter-default-behaviors
  [
   behavior/lonely
   behavior/hungry
   behavior/bored
   behavior/afraid
   behavior/collision
   behavior/boundaries
   ])