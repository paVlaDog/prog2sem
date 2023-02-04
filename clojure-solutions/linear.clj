(defn isV [args] (and (vector? args) (every? number? args)))
(defn isM [args] (every? #(and (isV %) (== (count %) (count (first args)))) args))
(defn opDifArgs [firstOp, secOp, first, lastArgs] (let [sec (apply secOp lastArgs)] (mapv (partial firstOp sec) first)))
(defn lRed [func, args] (reduce func (first args) (rest args)))
(defn vOp [op, args] {:pre [(isM args)] :post [(isV %)]} (vec (apply mapv op args)))
(defn mOp [op, args] {:pre [(every? #(and (vector? %) (isM %) (== (count %) (count (first args)))) args)] :post [(isM %)]}
  (vec (apply mapv op args)))
(defn preSimplex [args] (every? #(or (number? %) (and (vector? %) (== (count (first args)) (count %))
  (number? (first %))) (and (vector? %) (== (count (first args)) (count %)) (== (count %) (count (first %))))) args))
(defn xOp [op, sympOp, args] {:pre [(preSimplex args)] :post [(preSimplex args)]}
  (cond (number? (first args)) (apply op args) :else (vec (apply mapv sympOp args))))


(defn v+ [& args] (vOp + args))
(defn v- [& args] (vOp - args))
(defn v* [& args] (vOp * args))
(defn vd [& args] (vOp / args))
(defn m+ [& args] (mOp v+ args))
(defn m- [& args] (mOp v- args))
(defn m* [& args] (mOp v* args))
(defn md [& args] (mOp vd args))
(defn x+ [& args] (xOp + x+ args))
(defn x- [& args] (xOp - x- args))
(defn x* [& args] (xOp * x* args))
(defn xd [& args] (xOp / xd args))
(defn scalar [& args] {:pre [(isM args)] :post [(number? %)]} (apply + (apply v* args)))
(defn v*s [v & sArgs] {:pre [(isV v) (every? number? sArgs)] :post [(isV %)]} (opDifArgs * * v sArgs))
(defn m*s [m & sArgs] {:pre [(isM m) (every? number? sArgs)] :post [(isM %)]} (opDifArgs #(v*s %2 %1) * m sArgs))
(defn m*v [m & vArgs] {:pre [(isM m) (every? isV vArgs)] :post [(isV %)]} (opDifArgs scalar v* m vArgs))
(defn det2 [mas1 mas2 a b] (- (* (nth mas1 a) (nth mas2 b)) (* (nth mas1 b) (nth mas2 a))))
(defn vect [& args] {:pre [(isM args)] :post [(isV %)]}
  (lRed #(vector (det2 %1 %2 1 2) (det2 %1 %2 2 0) (det2 %1 %2 0 1)) args))
(defn transpose [a] {:pre [(isM a)] :post [(isM %)]} (apply mapv vector a))
(defn m*m [& args] {:pre [(every? #(and (vector? %) (isM %)) args)] :post [(isM %)]}
  (lRed #(mapv (fn [ai] (mapv (partial scalar ai) (transpose %2))) %1) args))

;
;(println (x+ 1 2))
;(println (x+ [1] [2]))
;(println (x+ [1 2] [2 3]))
;(println (x+ [[1 2] [3]] [[2 3] [4]]))
;
;(println (vd []))
;(println (md [[]]))
;(println (v+ [1 2 3] [4 5 6] [7 8 9]))
;(println (v- [1 2 3] [4 5 6] [7 8 9]))
;(println (v* [1 2 3] [4 5 6] [7 8 9]))
;(println (vd [1 2 3] [4 5 6] [7 8 9]))
;(println (scalar [1 2 3] [4 5 6] [7 8 9] ))
;(println (vect [1 2 3] [4 5 6]))
;(println (v*s [1 2 3] 5))
;
;(println (m+ [[1 2] [3 4]] [[5 6] [7 8]] [[5 6] [7 8]]))
;(println (m- [[1 2] [3 4]] [[5 6] [7 8]] [[5 6] [7 8]]))
;(println (m* [[1 2] [3 4]] [[5 6] [7 8]] [[5 6] [7 8]]))
;(println (md [[1 2] [3 4]] [[5 6] [7 8]] [[5 6] [7 8]]))
;(println (m*s [[1 2] [3 4]] 5 6))
;(println (m*v [[1 2] [3 4]] [5 6]))
;(println (m*m [[1 2] [3 4]] [[5 6] [7 8]]))
;(println (m*m (vector (vector 4.4)) (vector (vector 8.4 5.4))))
;(println (transpose [[1 2 3] [4 5 6] [7 8 9]]))
;(println (transpose [[1 2 3]]))
;
;
;
;
;
;
;
