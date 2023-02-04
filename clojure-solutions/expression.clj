(load-file "parser.clj")
(load-file "proto.clj")

(def constant constantly)
(defn variable [name] (fn [vars] (get vars name)))
(defn operation [func] (fn [& operands] (fn [vars] (apply func (map #(% vars) operands)))))
(def add (operation +))
(def subtract (operation -))
(def multiply (operation *))
(def divide (operation (fn ([operand] (/ 1.0 operand)) ([operand & restOp] (/ (double operand) (apply * restOp))))))
(def negate (operation -))
(def mean (operation (fn [& operands] (/ (apply + operands) (count operands)))))
(def varn (operation (fn [& operands] (- (/ (apply + (map #(* % %) operands)) (count operands))
                                         (#(* % %) (/ (apply + operands) (count operands)))))))

(def mapOp
  {'+ add
   '- subtract
   '* multiply
   '/ divide
   'negate negate
   'mean mean
   'varn varn})

(defn parseExpr [element] (cond
                            (number? element) (constant element)
                            (symbol? element) (variable (name element))
                            (list? element) (apply (get mapOp (first element)) (mapv parseExpr (rest element)))))

(def parseFunction (comp parseExpr read-string))

(def evaluate (method :eval))
(def toString (method :toStr))
(def toStringInfix (method :toStrInfix))
(def diff (method :diffi))
(def _sign (field :sign))
(def _op (field :op))
(def _type (field :type))
(def _diffOp (field :diffOp))
(def _args (field :args))

(def arity0 0)
(def arity1 1)
(def arityAny 2)

(defn Oper [sign type op diffOp] {
   :sign sign :type type :op op :diffOp diffOp
   :eval (fn [this vars] (cond
           (= (_type this) arity0) ((_op this) this vars)
           :else (apply (_op this) (map #(evaluate % vars) (_args this)))))
   :toStr (fn [this] (cond
           (= (_type this) arity0) (str ((_sign this) this))
           :else (str "(" (_sign this) " " (clojure.string/join " " (mapv #(toString %) (_args this))) ")")))
   :toStrInfix (fn [this] (cond
           (= (_type this) arity0) (str ((_sign this) this))
           (= (_type this) arity1) (str (_sign this) "(" (toStringInfix (first (_args this))) ")")
           :else (str "(" (clojure.string/join (str " " (_sign this) " ") (mapv #(toStringInfix %) (_args this))) ")")))
   :diffi (fn [this var] (cond
           (= (_type this) arity0) ((_diffOp this) this var)
           :else (apply (_diffOp this) this var (mapv #(diff % var) (_args this)))))
   })

(defn creator [sign type op diffOp] (let [proto {:prototype (Oper sign type op diffOp)}]
                                      (fn [& args] (assoc (get proto :prototype) :args args))))

(def Constant (creator #(first (_args %)) arity0 (fn [this vars] (first (_args this))) (fn [this var] (Constant 0))))
(def Variable (creator #(first (_args %)) arity0
                       #(get %2 (str (first (clojure.string/lower-case (first (_args %1))))))
                       (fn [this var] (if (= (str (first (clojure.string/lower-case
                                                           (first (_args this))))) var) (Constant 1) (Constant 0)))))
(def Negate (creator "negate" arity1 - (fn [_1 _2 & args] (apply Negate args))))
(def Add (creator "+" arityAny + (fn [_1 _2 & args] (apply Add args))))
(def Subtract (creator "-" arityAny - (fn [_1 _2 & args] (apply Subtract args))))
(def Multiply (creator "*" arityAny * (fn [this var & args] (apply (fn
                       ([a] (diff a var))
                       ([a b] (Add (Multiply (diff a var) b) (Multiply (diff b var) a)))
                       ([a b & other] (diff (Multiply (Multiply a b) (apply Multiply other)) var)))
                     (_args this)))))
(def Divide (creator "/" arityAny (fn ([args] (/ 1.0 args)) ([args & restArgs] (/ (double args) (apply * restArgs))))
                     (fn [this var & args] (apply (fn
                       ([a] (Negate (Divide (diff a var) (Multiply a a))))
                       ([a b] (Divide (Subtract (Multiply (diff a var) b) (Multiply (diff b var) a)) (Multiply b b)))
                       ([a b & other] (diff (Divide (Divide a b) (apply Multiply other)) var)))
                     (_args this)))))
(def Mean (creator "mean" arityAny
                   (fn [& args] (/ (apply + args) (count args))) (fn [_1 _2 & args] (apply Mean args))))
(def Varn (creator "varn" arityAny
                   (fn [& operands] (- (/ (apply + (map #(* % %) operands))
                                          (count operands)) (#(* % %) (/ (apply + operands) (count operands)))))
                   (fn [this var & args] (diff (Subtract (apply Mean (map #(Multiply % %) (_args this)))
                                                (Multiply (apply Mean (_args this)) (apply Mean (_args this)))) var))))
(def IPow (creator "**" arityAny
                   (fn [a b] (Math/pow a b)) (fn [_1 _2 & args] (apply IPow args))))
(def ILog (creator "//" arityAny (fn [a b] (/ (Math/log (Math/abs b)) (Math/log (Math/abs a))))
                   (fn [_1 _2 & args] (apply ILog args))))

(def mapObjOp
  {'+ Add
   '- Subtract
   '* Multiply
   '/ Divide
   'negate Negate
   'mean Mean
   'varn Varn
   })

(defn parseExprObj [element] (cond
                            (number? element) (Constant element)
                            (symbol? element) (Variable (name element))
                            (list? element) (apply (get mapObjOp (first element)) (mapv parseExprObj (rest element)))))

(def parseObject (comp parseExprObj read-string))

;(declare *parseBracers *parseOperands *parseExpr)
;
;(def *all-chars (mapv char (range 0 256)))
;(defn *filterSymb [func] (+char (apply str (filter func *all-chars))))
;(def *naturalNum  (+str (+plus (*filterSymb #(Character/isDigit %)))))
;(def *ws (+ignore (+star (*filterSymb #(Character/isWhitespace %)))))
;(def *number (+map read-string (+str (+seq (+opt (+char "-")) *naturalNum (+opt (+char ".")) (+opt *naturalNum)))))
;
;(defn *parseWords [str1] (apply +seq (map #(+char (str %)) (seq str1))))
;(defn *parseSign [opMap] (+map (comp (partial get opMap) #(apply str %)) (apply +or (map *parseWords (keys opMap)))))
;(def *parseConstant (+map Constant *number))
;(def *parseVariable (+map (comp Variable #(apply str %)) (+plus (+char "xyzXYZ"))))
;(def *parseNegate (+map Negate (+seqn 1 (*parseWords "negate") *ws (delay *parseOperands) *ws)))
;(def *parseOperands (+or *parseConstant *parseNegate  *parseVariable (delay *parseBracers)))
;(defn *parseLeftExp [opMap *opDown] (let [*opSign (*parseSign opMap)] (+map #(reduce (fn [a b] (b a)) (first %) (rest %))
;              (+seqf cons *ws *opDown *ws (+star (+map (fn [[a b]] #(a % b)) (+seq *ws *opSign *ws *opDown *ws)))))))
;(defn *parseRightExp [opMap *opDown] (let [*opSign (*parseSign opMap)]
;              (+or (+map (fn [[a b c]] (b a c)) (+seq *opDown *ws *opSign *ws (delay (*parseRightExp opMap *opDown)))) *opDown)))
;(def *parseBracers (+seqn 1 (+char "(") (delay *parseExpr) (+char ")")))
;
;(def *parseExpr (*parseLeftExp {"+" Add "-" Subtract}
;                      (*parseLeftExp {"*" Multiply "/" Divide}
;                         (*parseRightExp {"**" IPow "//" ILog}
;                            *parseOperands))))
;(def parseObjectInfix (+parser *parseExpr))

(declare *parseBracers *parseOperands *parseExpr)

(def *all-chars (mapv char (range 0 256)))
(defn *filterSymb [func] (+char (apply str (filter func *all-chars))))
(def *naturalNum  (+str (+plus (*filterSymb #(Character/isDigit %)))))
(def *ws (+ignore (+star (*filterSymb #(Character/isWhitespace %)))))
(def *number (+map read-string (+str (+seq (+opt (+char "-")) *naturalNum (+opt (+char ".")) (+opt *naturalNum)))))

(defn *parseSign [opMap] (+map (comp (partial get opMap) #(apply str %)) (apply +or
                       (map (fn [str1] (apply +seq (map #(+char (str %)) (seq str1)))) (keys opMap)))))
(def *parseConstant (+map Constant *number))
(def *parseVariable (+map (comp Variable #(apply str %)) (+plus (+char "xyzXYZ"))))
(def *parseNegate (+map Negate (+seqn 1 ((fn [str1] (apply +seq (map #(+char (str %)) (seq str1))))
                                         "negate") *ws (delay *parseOperands) *ws)))
(def *parseOperands (+or *parseConstant *parseNegate  *parseVariable (delay *parseBracers)))
(defn *parseLeftExp [opMap *opDown] (let [*opSign (*parseSign opMap)] (+map #(reduce (fn [a b] (b a)) (first %) (rest %))
              (+seqf cons *ws *opDown *ws (+star (+map (fn [[a b]] #(a % b)) (+seq *ws *opSign *ws *opDown *ws)))))))
(defn *parsePowLog [*opDown] (let [*opSign (*parseSign {"**" IPow "//" ILog})]
              (+or (+map (fn [[a b c]] (b a c)) (+seq *opDown *ws *opSign *ws (delay (*parsePowLog *opDown)))) *opDown)))
(def *parseBracers (+seqn 1 (+char "(") (delay *parseExpr) (+char ")")))

(def *parseExpr (*parseLeftExp {"+" Add "-" Subtract}
                      (*parseLeftExp {"*" Multiply "/" Divide}
                         (*parsePowLog *parseOperands))))
(def parseObjectInfix (+parser *parseExpr))
