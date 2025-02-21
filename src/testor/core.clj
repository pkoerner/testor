(ns testor.core
  (:require [clojure.string :as str]
            [clojure.pprint :as pp]))

(defn gen-simplified-testform 
  [[sym & tail :as form] [_quote-sym expected]]
  (cond (and (= sym '=) (= expected true)) 
          form
        (and (= sym '=) (= expected false))
          `(~'not= ~@tail)
        (and (= sym 'not=) (= expected true)) 
          form
        (and (= sym 'not=) (= expected false))
          `(~'= ~@tail)
        (and (#{'< '<= '> '>=} sym) (= expected true))
          form
        (and (#{'< '<= '> '>=} sym) (= expected false))
          `(~'not ~form)
        (and (= \? (last (name sym))) (= expected true))
          form
        (and (= \? (last (name sym))) (= expected false))
          `(~'not ~form)
        :otherwise `(~'= ~form ~expected)))

(defn- gen-test 
  ([form] (gen-test form (with-meta `(quote ~(eval form)) {:src :org.clojars.pkoerner/testor})))
  ([form expected]
   (gen-test (symbol (str (name (first form)) "-test-" (quot (System/currentTimeMillis) 1000) "-" (name (gensym "")))) form expected))
  ([testname form expected]
   (gen-test testname "this was deemed correct during development" form expected))
  ([testname description form expected]
   (let [testform (if (= :org.clojars.pkoerner/testor (:src (meta expected)))
                    (gen-simplified-testform form expected)
                    `(~'= ~form ~expected))]
  `(~'deftest ~testname
     (~'testing ~description
       (~'is ~testform))))))

;; stolen from clojure.tools.namespace.move
(defn- ns-file-name [sym]
  (str (-> (name sym)
           (str/replace "-" "_")
           (str/replace "." java.io.File/separator))
       "_test"
       ".clj"))

(defn- get-test-file []
  (clojure.java.io/file "test/" (ns-file-name (ns-name *ns*))))

(defn- gen-test-ns []
  `(~'ns ~(symbol (str (ns-name *ns*) "-test"))
     (:require [~'clojure.test :refer :all]
               [~(ns-name *ns*) :refer :all])))

(defn- add-test! [macroexpanded-test]
  (let [file (get-test-file)]
    (when-not (.exists file)
      (.mkdirs (.getParentFile file))
      (spit file (with-out-str (pp/pprint (gen-test-ns)))))
    (spit file (str \newline (with-out-str (pp/pprint macroexpanded-test))) :append true)))

(defmacro fixate!! 
  "Add a test of the form (= form (eval form)) 
  or (= form expected), depending on number of arguments.
  Test will be added to a file test/your/awesome/name_space-test.clj"
  ([form] (add-test! (gen-test form)))
  ([form expected] (add-test! (gen-test form expected)))
  ([testname form expected] (add-test! (gen-test testname form expected))) 
  ([testname description form expected] (add-test! (gen-test testname description form expected))) )

(defmacro fixate-all!! 
  "Convenience macro. Will call (fixate!! form) on each of the forms."
  [& forms]
  `(do ~@(for [f# forms] `(fixate!! ~f#))))


(comment 
(fixate!! (zero? 1))
(fixate!! (zero? 0))
(fixate!! (first [1 2 3]))
(fixate!! (first [1 2 3]) (+ 1 1))
(fixate!! (map inc [1 2 3]))
(fixate!! foo-test (first [1 2 3]) 1)
(fixate!! foo-test "this tests stuff" (first [1 2 3]) 1)
)
