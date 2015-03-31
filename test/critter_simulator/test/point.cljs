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
(def d-up       0)
(def d-right    (* pi .5))
(def d-down     pi)
(def d-left     (* pi 1.5))

(def polar {:r 10 :angle d-right})

; accomodate fuzzy rounding
(defn eq-ish [a b]
  (> 0.0001 (Math/abs (- a b))))

(deftest cartesian<->polar
  (let [cpc (-> p-middle    point/cartesian->polar point/polar->cartesian)
        pcp (-> polar       point/polar->cartesian point/cartesian->polar)]
    (is (eq-ish (first p-middle)     (first cpc)))
    (is (eq-ish (second p-middle)    (second cpc)))
    (is (eq-ish (:r polar)           (:r pcp)))
    (is (eq-ish (:angle polar)       (:angle pcp)))))

(deftest bearing
  (is (= d-up (point/bearing p-middle p-up)))
  (is (= d-down (point/bearing p-middle p-down)))
  (is (= d-left (point/bearing p-middle p-left)))
  (is (= d-right (point/bearing p-middle p-right)))
  )