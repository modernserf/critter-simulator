(ns ^:figwheel-always critter-simulator.core
    (:require-macros [cljs.core.async.macros :refer [go go-loop]])
    (:require
        [cljs.core.async :as async :refer [<! >!]]
        [reagent.core                 :as reagent]
        [critter-simulator.critter-alt    :as critter]
        [critter-simulator.food       :as food]
        [critter-simulator.views.core :as views]
         [critter-simulator.point :as point]))

(enable-console-print!)

(def base-env
  {:width 500 :height 500 :channel (async/chan)})

(def base-critters
  (critter/make-list
    [["Slipper"    {:color [:black :white :orange]}]
     ["Allegra"    {:color [:black :orange :white]}]
     ["Totoro"     {:color [:white :black :black]}]
     ["Squeaky"    {:color [:orange :orange :orange]}]
     ["Sarah Jane" {:color [:black :black :black]}]
     ["Gizmo"      {:color [:white :orange :orange]}]
     ["Twitch"     {:color [:black :black :black]}]
     ["Onigiri"    {:color [:black :white :white]}]
     ["Pui Pui"    {:color [:orange :white :white]}]
  ] base-env))

(defn init-env []
  (atom (assoc base-env :critters base-critters)))

(defn in-bounds? [[x y] {:keys [width height]}]
  (and (< 0 x width) (< 0 y height)))

(defn simple-collided? [{:keys [position name]} env]
  (let [min-distance 20]
    (some 
      (fn [[n x]]
        (and (not= name n)  
             (< (point/distance position (:position @x))
                min-distance))) 
      (:critters env))))

(defn handle-critter! [c env next-state]
  (let [state  @(:state c)
        name   (:name state)
        state' (merge state next-state)]
    ; update critter in env
    (reset! (-> env deref :critters (get name)) state')
    (go
      (<! (async/timeout 100)) 
      (>! (:channel c) 
          (if (and (in-bounds? (:position state') @env)
                   (not (simple-collided? state' @env)))
            [:ok state']
            [:collision state'])))
    ; (println env name next-state)
    env))

(defn render [env]
  (reagent/render-component [views/module-app-root env]
                            (. js/document (getElementById "app"))))

(def render-ch
  "render-ch expects env"
  (let [ch (async/chan (async/dropping-buffer 1))]
    (go-loop []
      (<! (async/timeout 100))
      (render (<! ch))
      (recur))
    ch))



(defn make-world []
  (let [env    (init-env)
        ch    (:channel base-env)]
    ; (println "env is" env)
    (render env)
    (go-loop []
      (let [[msg item params] (<! ch)]
        ; (println
        ;   "world received " msg
        ;   "for" (-> item :state deref :name)
        ;   "to" params)
        (case msg
          :critter-move (>! render-ch (handle-critter! item env params)))
        (recur)))
    ))

(defonce world (make-world))
