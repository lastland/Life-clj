(ns Life-clj.core
  (:import (java.awt Color Dimension)
           (javax.swing JPanel JFrame Timer JOptionPane)
           (java.awt.event ActionListener KeyListener))
  (:gen-class))
(def ^:dynamic width 20)
(def ^:dynamic height 20)
(def ^:dynamic life-size 20)
(def ^:dynamic life-color (Color. 210 50 9))
(def ^:dynamic gen-millis 1000)
(def directions [[-1 -1] [-1 0] [-1 1] [0 1]
                 [1 1] [1 0] [1 -1] [0 -1]])
(defn create-lives []
  (vec (take (* height width) (cycle [0]))))
(defn gen-initial-state []
  (let [random (java.util.Random.)]
        (vec (for [i (take height (iterate inc 0))
                   j (take width (iterate inc 0))]
               (.nextInt random 2)))))
(defn get-life [lives x y]
  (lives (+ (* x width) y)))
(defn in-range? [x y]
  (and (>= x 0) (>= y 0)
       (< x width) (< y height)))
(defn add-points [& pts]
  (vec (apply map + pts)))
(defn get-neighbors [x y]
  (filter #(apply in-range? %) (map add-points directions (cycle [[x y]]))))
(def get-neighbors (memoize get-neighbors))
(defn get-live-neighbors-cnt [lives x y]
  (apply + (map #(apply (partial get-life lives) %) (get-neighbors x y))))
(defn die-for-under-population? [live-neighbors-cnt]
  (< live-neighbors-cnt 2))
(defn die-for-overcrowding? [live-neighbors-cnt]
  (> live-neighbors-cnt 3))
(defn reproduct? [live-neighbors-cnt]
  (= live-neighbors-cnt 3))
(defn live? [lives x y]
  (let [life (get-life lives x y)
        live-neighbors-cnt (get-live-neighbors-cnt lives x y)]
    (if (not (zero? life))
      (not (or (die-for-under-population? live-neighbors-cnt)
               (die-for-overcrowding? live-neighbors-cnt)))
      (reproduct? live-neighbors-cnt))))
(defn get-next-gen [lives]
  (vec (for [i (take height (iterate inc 0))
             j (take width (iterate inc 0))]
         (if (live? lives i j) 1 0))))
(defn update-game [lives]
  (dosync (ref-set lives (get-next-gen @lives))))
(defn point-to-screen-rect [pt]
  (map (partial * life-size) [(pt 0) (pt 1) 1 1]))
(defn fill-point [g pt color]
  (let [[x y width height] (point-to-screen-rect pt)]
    (.setColor g color)
    (.fillRect g x y width height)))
(defn paint [g lives]
  (dotimes [i height]
    (dotimes [j width]
      (when (= 1 (get-life lives i j))
        (fill-point g [i j] life-color)))))
(defn game-panel [frame lives]
  (proxy [JPanel ActionListener] []
    (paintComponent [g]
      (proxy-super paintComponent g)
      (paint g @lives))
    (actionPerformed [e]
      (update-game lives)
      (.repaint this))
    (getPreferredSize []
      (Dimension. (* (inc width) life-size)
                  (* (inc height) life-size)))))
(defn game []
  (let [lives (ref (gen-initial-state))
        frame (JFrame. "Conway's Game of Life")
        panel (game-panel frame lives)
        timer (Timer. gen-millis panel)]
    (doto panel
      (.setFocusable true))
    (doto frame
      (.add panel)
      (.pack)
      (.setVisible true)
      (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE))
    (.start timer)
    [lives timer]))
(defn -main [& args]
  (game))