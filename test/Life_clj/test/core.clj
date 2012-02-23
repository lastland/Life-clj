(ns Life-clj.test.core
  (:use [Life-clj.core])
  (:use [clojure.test]))
(def test-width 4)
(def test-height 4)
(def test-lives [1 1 0 0
                 0 1 0 0
                 1 1 0 0
                 0 0 1 0])
(deftest test-get-life
  (binding [width test-width height test-height]
    (is (= 1 (get-life test-lives 0 0)))
    (is (= 1 (get-life test-lives 1 1)))
    (is (= 1 (get-life test-lives 2 0)))
    (is (= 0 (get-life test-lives 3 3)))
    (is (= 1 (get-life test-lives 3 2)))))
(deftest test-in-range?
  (binding [width test-width height test-height]
    (is (true? (in-range? 0 0)))
    (is (true? (in-range? 3 3)))
    (is (true? (in-range? 1 2)))
    (is (false? (in-range? -1 0)))
    (is (false? (in-range? 0 -1)))
    (is (false? (in-range? -1 -1)))
    (is (false? (in-range? 4 4)))))
(deftest test-add-points
  (is (= [1 2] (add-points [1 2])))
  (is (= [4 4] (add-points [1 2] [3 2])))
  (is (= [5 6] (add-points [1 2] [3 4] [1 0])))
  (is (= [0 0] (add-points [1 1] [-1 -1] [2 3] [-2 -3]))))
(deftest test-get-neighbors
  (binding [width test-width height test-height]
    (is (= (get-neighbors 0 0) '([0 1] [1 1] [1 0])))
    (is (= (get-neighbors 0 0) '([0 1] [1 1] [1 0])))
    (is (= (get-neighbors 0 1) '([0 2] [1 2] [1 1] [1 0] [0 0])))
    (is (= (get-neighbors 3 3) '([2 2] [2 3] [3 2])))
    (is (= (get-neighbors 2 2) '([1 1] [1 2] [1 3] [2 3] [3 3] [3 2] [3 1] [2 1])))))
(deftest test-get-live-neighbors-cnt
  (binding [width test-width height test-height]
    (is (= 2 (get-live-neighbors-cnt test-lives 0 0)))
    (is (= 4 (get-live-neighbors-cnt test-lives 1 1)))
    (is (= 1 (get-live-neighbors-cnt test-lives 3 3)))
    (is (= 3 (get-live-neighbors-cnt test-lives 1 2)))))
(deftest test-die-for-under-population?
  (is (true? (die-for-under-population? 0)))
  (is (true? (die-for-under-population? 1)))
  (is (false? (die-for-under-population? 4)))
  (is (false? (die-for-under-population? 2)))
  (is (false? (die-for-under-population? 3))))
(deftest test-die-for-overcrowding?
  (is (true? (die-for-overcrowding? 4)))
  (is (true? (die-for-overcrowding? 5)))
  (is (false? (die-for-overcrowding? 0)))
  (is (false? (die-for-overcrowding? 1)))
  (is (false? (die-for-overcrowding? 2))))
(deftest test-reproduct?
  (is (true? (reproduct? 3)))
  (is (false? (reproduct? 2)))
  (is (false? (reproduct? 4))))
(deftest test-live?
  (binding [width test-width height test-height]
    (is (true? (live? test-lives 0 0)))
    (is (false? (live? test-lives 3 2)))
    (is (false? (live? test-lives 1 1)))
    (is (true? (live? test-lives 1 2)))))
(deftest test-point-to-screen-rect
  (is (= [0 0 life-size life-size] (point-to-screen-rect [0 0]))))