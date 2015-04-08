(ns ^:figwheel-always critter-simulator.critter-alt
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :as async :refer [<! >!]]

            [critter-simulator.point :as point]))

; message format
; [:name {params}]

(defn next-pos [c {:keys [position velocity bearing]}]
  (point/add
    position
    (point/polar->cartesian {:r velocity
                             :angle bearing})))

(defn body [self ch-out]
  (let [ch-in (async/chan)]
    (go-loop [[msg params] (<! ch-in)]
      (let []
        (case msg
          :move (>! ch-out [:critter-move
                            self
                            {:position (next-pos self params)}]))
        (recur (<! ch-in))))
    ch-in))

(defn avoid-obstacle [self ch-out]
  (let [ch-in (async/chan)]
    (go-loop []
      (let [[msg params] (<! ch-in)]
        (case msg
          :obstacle (>! ch-out [:move
                                self
                                {:bearing (+ Math/PI (:bearing params))}])
          :default)
        (recur)))
    ch-in))

(defn executive [ch-in self ch-out]
  (go-loop []
    (<! (async/timeout 100))
    (>! ch-out [:move @(:state self)])
    (recur))
  ch-in)

(defn init-state [name props env]
  (atom (merge
          {:color [:white :white :white]
           :name name
           :position (point/random env)
           :bearing (rand (* 2 Math/PI))
           :velocity 10
           :hungry 0
           :cowardly 0
           :lonely 0 }
          props)))

(defrecord Critter [state channel])

(defn make [[name props] env]
  (let [ch (async/chan)
        c (Critter. (init-state name props env) ch)
        world-ch (:channel env)
        body-ch (body c world-ch)
        exec-ch (executive ch c body-ch)]
    c))

(defn make-list [cs env]
  (let [vs (map #(make % env) cs)]
    (reduce #(assoc %1 (:name @(:state %2)) (:state %2)) {} vs)))

