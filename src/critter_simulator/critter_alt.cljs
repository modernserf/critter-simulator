(ns ^:figwheel-always critter-simulator.critter-alt
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :as async :refer [<! >!]]

            [critter-simulator.point :as point]))




; TODO: update based on last timestamp?
; (defn next-state [state params]
;   (let [s (merge @state params)
;         next-pos (point/add
;                    (:position s)
;                    (point/polar->cartesian {:r (:velocity s)
;                                             :angle (:bearing s)}))]
;     (assoc s :position next-pos)))


(defn set-state! [c next-state]
  (swap! (:state c) merge next-state))

; message format
; [:name {params}]

; move behavior
; (defn move [c]
;   (let [ch (chan)
;         parent-ch (:channel c)]
;     (go (while true
;           (let [[msg state] (<! ch)]
;             ; (println "move received " msg)
;             (case msg
;               :state
;                 (do
;                   (>! parent-ch [:set (next-state state nil)])
;                   (when (> (:velocity @state) 0)
;                     (js/setTimeout (fn []
;                                      (put! ch [:state state])) 100)))
;               :collision
;                 (do

;                     (>! parent-ch
;                         [:set (next-state
;                                 state
;                                 {
;                                  :bearing   (+ Math/PI (:bearing @state))
;                                  :velocity 10
;                                  })
;                          ])
;                     ; (println (:velocity @state))
;                     ; (put! ch [:state state])
;                     )
;               :default ))))
;     ch))

(defn next-pos [c {:keys [position velocity bearing]}]
  (point/add
    position
    (point/polar->cartesian {:r velocity
                             :angle bearing})))


(defn body [self ch-out]
  (let [ch-in (async/chan)]
    (go-loop []
      (let [[msg params] (<! ch-in)]
        (println "body received" msg)
        (case msg
          :move (>! ch-out [:critter-move
                            self
                            {:position (next-pos self params)}]))
        (recur)))
    ch-in))



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
        body-ch (body c (:channel env))]
    (go-loop []
      (<! (async/timeout 100))
      (>! body-ch [:move @(:state c)])
      #_(recur))
    c))

(defn make-list [cs env]
  (let [vs (map #(make % env) cs)]
    (reduce #(assoc %1 (:name @(:state %2)) (:state %2)) {} vs)))

