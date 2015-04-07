(ns ^:figwheel-always critter-simulator.core
    (:require-macros [cljs.core.async.macros :refer [go go-loop]])
    (:require
        [cljs.core.async              :refer [chan <! >! put! timeout]]
        [reagent.core                 :as reagent   :refer [atom]]
        [critter-simulator.critter-alt    :as critter]
        [critter-simulator.food       :as food]
        [critter-simulator.views.core :as views]))

(enable-console-print!)

(defn debounce [in ms]
  (let [out (chan)]
    (go-loop [last-val nil]
      (let [val (if (nil? last-val) (<! in) last-val)
            timer (timeout ms)
            [new-val ch] (alts! [in timer])]
        (condp = ch
          timer (do (>! out val) (recur nil))
          in (recur new-val))))
    out))

(def base-env
  {:width 500 :height 500 :channel (chan 10)})

(def base-critters
  (critter/make-list
    [["Slipper"    {:color [:black :white :orange]}]
     ["Allegra"    {:color [:black :orange :white]}]
     ["Totoro"     {:color [:white :black :black]}]
     ["Squeaky"    {:color [:orange :orange :orange]}]
     ["Sarah Jane" {:color [:black :black :black]}]
     ["Gizmo"      {:color [:white :orange :orange]}]
     ["Twitch"     {:color [:black :black :black]}]
     ["Professor Popcorn" {:color [:orange :white :white]}]
     ["Jareth"      {:color [:orange :orange :orange]}]
     ["Onigiri"     {:color [:black :white :white]}]
     ["Pui Pui"     {:color [:orange :white :white]}]
  ] base-env))

(defn init-env []
  (atom (assoc base-env :critters base-critters)))

(defn in-bounds? [[x y] {:keys [width height]}]
  (and (< 0 x width) (< 0 y height)))




(defn handle-critter! [c env]
  (let [cstate (:state c)
        name   (:name c)]
    (swap! env assoc-in [:critters name] cstate)
    (when-not (in-bounds? (:position @cstate) @env)
      (put! (:channel c) [:collision]))
    env))

(defn render [env]
  (reagent/render-component [views/module-app-root env]
                            (. js/document (getElementById "app"))))

(def render-ch
  (let [ch (chan 10)
        d  (debounce ch 100)]
    (go (while true
          (let [env (<! d)]
            ; (println "render received " env)
            (render env))))
    ch))

(defn make-world []
  (let [env    (init-env)
        ch    (:channel base-env)]
    ; (println "env is" env)
    (go (while true
          (let [[msg params] (<! ch)]
            ; (println "world received msg")
            (case msg
              :critter (>! render-ch (handle-critter! params env))
              :default))))
    (put! render-ch env)
    ))

(defonce world (make-world))
