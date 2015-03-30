(ns ^:figwheel-always critter-simulator.test.point
  (:require
   [critter-simulator.point :as point]
   [cljs.test :refer-macros [deftest testing is run-tests]]))

(def pi Math/PI)

; points
(def p-middle   [50 50])
(def p-up       [50 0])
(def p-down     [50 100])
(def p-left     [0 50])
(def p-right    [100 50])

; directions (radians)
(def d-left     0)
(def d-down     (/ pi 2))
(def d-right    (- pi))
(def d-up       (- d-down))

(deftest bearing
  (is (= d-up (point/bearing p-middle p-up)))
  (is (= d-down (point/bearing p-middle p-down)))
  (is (= d-left (point/bearing p-middle p-left)))
  (is (= d-right (point/bearing p-middle p-right)))
  )