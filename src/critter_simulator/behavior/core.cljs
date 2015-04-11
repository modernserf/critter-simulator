(ns ^:figwheel-always critter-simulator.behavior.core
    (:require
        [critter-simulator.point   :as point]
        [critter-simulator.critter :as critter]
        [critter-simulator.food    :as food]
        [critter-simulator.protocols.collidable :as collidable]))

; increment/decrement state counter k
; TODO: increment based on time elapsed vs 1-per-round
(defn inc-state [c k] (update-in c [:state k] #(+ % 0.1)))
; decrement until 0
(defn dec-0 [x] (max 0 (- x 1)))
(defn dec-state [c k] (update-in c [:state k] dec-0))

(defn behavior [k pred-recover? fn-threshold]
  (fn [c env]
    (cond (pred-recover? c env)         (dec-state    c k)
          (critter/at-threshold? c k)   (fn-threshold c env)
          :else                         (inc-state    c k))))

(defn find-closest-critter [c cs]
  (let [pos     (:position c)
        sorted  (reduce #(assoc %1 (point/distance pos (:position %2)) %2)
                        (sorted-map)
                        (filter #(not= c %) cs))]
    (-> sorted vals first)))

(def distance-lonely 30)

(defn go-to-neighbor [c env]
  (let [closest (find-closest-critter c (:critters env))]
    (critter/set-destination
      c 10 (point/to-perimeter
             (:position c)
             (:position closest)
             distance-lonely))))

(defn near-critters? [a b dist]
  (cond (critter/eq? a b) nil
        :else (point/near? (:position a) (:position b) dist)))

(defn is-near-others? [c env]
  (some #(near-critters? c % (+ 10 distance-lonely)) (:critters env)))

(defn is-eating? [c env]
  (food/near? (:food env) (:position c)))

(defn go-to-food [c env]
  (critter/set-destination
      c 10 (point/to-perimeter
             (:position c)
             (:position (:food env))
             (+ 10 food/radius))))

(defn is-away-from-cursor? [c env]
  (not (and (:mouse env) (point/near? (:position c) (:mouse env) 100))))

(defn run-away [c env]
  (critter/set-destination c -20 (:mouse env)))

(defn is-excited? [c env] (not (critter/at-rest? c)))
(defn wander [c env]
  (critter/set-destination c 1 (point/random env)))

(defn poop [c env]
   ((:do-poop env) c)
   (assoc-in c [:state :bowel] 0))

(def lonely (behavior :lonely is-near-others?      go-to-neighbor))
(def hungry (behavior :hungry is-eating?           go-to-food))
(def afraid (behavior :afraid is-away-from-cursor? run-away))
(def poopy  (behavior :bowel  (fn [_ _] false)     poop))
(def bored  (behavior :bored  is-excited?          wander))

(defn critter-collisions [c env]
  (filter #(collidable/is-colliding? c %) (:critters env)))

(defn food-collisions [c env]
  (if (collidable/is-colliding? c (:food env))
    [(collidable/position (:food env))]
    []))

(defn collision [c env]
  (let [collisions      (concat (critter-collisions c env)
                                (food-collisions c env))
        collision-area  (point/center-of (map :position collisions))]
    (if (seq collisions)
      (if (critter/at-rest? c)
        (critter/set-destination c -10 collision-area)
        (critter/set-destination c 10 (critter/alter-bearing c 1)))
      c)))

(defn clamp [val' min' max'] (min max' (max min' val')))

(defn boundaries [c {:keys [width height]}]
  (critter/set-destination
    c 10 [(clamp (-> c :destination first) 5 (- width 5))
          (clamp (-> c :destination second) 5 (- height 5))]))
