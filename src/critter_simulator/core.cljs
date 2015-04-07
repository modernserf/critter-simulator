(ns ^:figwheel-always critter-simulator.core
    (:require-macros [cljs.core.async.macros :refer [go]])
    (:require
        [cljs.core.async              :refer [chan <! >!]]
        [reagent.core                 :as reagent   :refer [atom]]
        [critter-simulator.critter-alt    :as critter]
        [critter-simulator.food       :as food]
        [critter-simulator.views.core :as views]))

(enable-console-print!)



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

(defn boundaries [env ch]
  )



(defn handle-critter [critter env]
  (println critter)
  (swap! env assoc-in [:critters (:name critter)] critter)
  ; replace changed critter
  env)




(defn render [env]
  (reagent/render-component [views/module-app-root env]
                            (. js/document (getElementById "app"))))

(defn make-world []
  (let [env    (init-env)
        ch    (:channel base-env)
        render-ch (chan 10)]
    (render env)
    (go (while true
          (let [[msg params] (<! ch)]
            (case msg
              :critter (>! render-ch (handle-critter params env))
              :default))
          (let [[msg next-env] (<! render-ch)]
            (render next-env))))))

(defonce world (make-world))
