(ns ^:figwheel-always critter-simulator.critter-alt
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [chan <! >! put! close!]]
            [critter-simulator.point :as point]))


; message format
; [:name {params}]

; move behavior

(defn timeout [ms]
  (let [c (chan)]
    (js/setTimeout (fn [] (close! c)) ms)
    c))


; TODO: update based on last timestamp?
(defn next-state [state params]
  (let [s (merge @state params)
        next-pos (point/add
                   (:position s)
                   (point/polar->cartesian {:r (:velocity s)
                                            :angle (:bearing s)}))]
    (assoc s :position next-pos)))


(defn set-state! [c next-state]
  (swap! (:state c) merge next-state))

(defn move [c]
  (let [ch (chan)
        parent-ch (:channel c)]
    (go (while true
          (let [[msg state] (<! ch)]
            (println "move received " msg)
            (case msg
              :state
                (do 
                  (>! parent-ch [:set (next-state state nil)])
                  (when (> (:velocity @state) 0)
                    (js/setTimeout #(put! ch [:state state]) 400)))
              :collision
                (let [s @state]
                    (>! parent-ch
                        [:set (next-state
                                state
                                {
                                 ; :bearing   (+ Math/PI (:bearing s))
                                 :velocity 0
                                 })
                         ])
                    (>! ch [:state state]))
              :default ))))
    ch))

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
  (let [ch (chan 5)
        out-ch (:channel env)
        c (Critter. (init-state name props env) ch)
        mv-ch (move c)]
    ; add self to state
    ; (swap! (:state c) assoc :self c)
    (go (while true
          (let [[msg data] (<! ch)]
            ; (println "critter received " msg)
            (case msg
              :set
                (do
                  (set-state! c data)
                  (>! out-ch [:critter c]))
              :collision
                (>! mv-ch [:collision (:state c)])
              :default))
          ))
    (put! mv-ch [:state (:state c)])
    c))

(defn make-list [cs env]
  (let [vs (map #(make % env) cs)]
    (reduce #(assoc %1 (:name @(:state %2)) (:state %2)) {} vs)))

