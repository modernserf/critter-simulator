(ns ^:figwheel-always critter-simulator.critter
    (:require [critter-simulator.point :as point]))

(def init-critter-state {:hungry 0 :lonely 0 :afraid 0 :bowel 0 :bored 0})

; color is triple of head / torso / body
(defn make-colors [color] {:color [color color color]})

(def critter-props
  {:default   {:hungry 5 :afraid 5 :lonely 5 :bored 5
               :color (make-colors :white)}
   :hungry    {:hungry 7}
   :cowardly  {:afraid 7}
   :friendly  {:afraid 2 :lonely 7}
   :black     (make-colors :black)
   :orange    (make-colors :orange)})

(defn make-props [options]
  (reduce #(merge %1 (cond (keyword? %2) (%2 critter-props) :else %2))
          (:default critter-props)
          options))

(defrecord Critter [name props state
                    position destination
                    velocity bearing
                    behaviors])

(defn make [params env]
  (->Critter (first params) (make-props (rest params)) init-critter-state
             (point/random env) (point/random env)
             0 0
             (:behaviors env)))

(defn make-list [cs env]
  (map #(make % env) cs))

(defn do-behaviors [c env]
  (reduce #(%2 %1 env) c (:behaviors c)))

(defn next-position [c]
  (let [pos   (:position c)
        dest  (:destination c)
        p     (point/cartesian->polar (point/offset pos dest))
        vel   (min (:r p) (:velocity c))
        o'    (point/polar->cartesian (assoc p :r vel))
        pos'  (point/add pos o')
        ]
    (assoc c  :position pos')))

(defn eq? [a b] (= (:name a) (:name b)))

(defn at-rest? [c]
  (point/near? (:position c) (:destination c) 1))

(defn bearing [c] (point/bearing (:position c) (:destination c) ))

(defn set-destination [c v dest]
  (cond (>= v 0)  (assoc c  :velocity v :destination dest)
        :else     (let [pos     (:position c)
                        o-inv   (point/offset dest pos)
                        dest'   (point/add pos o-inv)
                    ]
                    (assoc c :velocity    (- v)
                             :destination dest'))))