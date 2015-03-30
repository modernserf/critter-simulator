(ns ^:figwheel-always critter-simulator.point)

(defn offset [[x1 y1] [x2 y2]] [(- x1 x2) (- y1 y2)])
(defn add [[x1 y1] [x2 y2]] [(+ x1 x2) (+ y1 y2)])
(defn distance' [[x y]] (Math/sqrt (+ (* x x) (* y y))))
(defn bearing' [[x y]] (Math/atan2 (- y) x))

(defn cartesian->polar [c]
  {:r     (distance' c)
   :angle (bearing' c)})

(defn polar->cartesian [{:keys [r angle]}]
    [(* r (Math/cos angle))
     (- (* r (Math/sin angle)))])

(defn random [{:keys [width height]}]
  [(rand-int width) (rand-int height)])

(defn distance [a b] (distance' (offset a b)))
(defn bearing [from to] (bearing' (offset from to)))

(defn near? [a b dist] (> dist (distance a b)))

(defn mean [xs] (/ (apply + xs) (count xs)))

(defn center-of [ps]
  [(mean (map first ps)) (mean (map second ps))])