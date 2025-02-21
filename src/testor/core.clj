(ns testor.core
  (:require [clojure.string :as str]
            [clojure.pprint :as pp]))

(defn- gen-test 
  ([form] (gen-test form `(quote ~(eval form))))
  ([form expected]
   (gen-test (symbol (str (name (first form)) "-test-" (quot (System/currentTimeMillis) 1000) "-" (name (gensym "")))) form expected))
  ([testname form expected]
   (gen-test testname "this was deemed correct during development" form expected))
  ([testname description form expected]
  `(~'deftest ~testname
     (~'testing ~description
       (~'is (~'= ~form ~expected))))))

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
(fixate!! (first [1 2 3]))
(fixate!! (first [1 2 3]) (+ 1 1))
(fixate!! (map inc [1 2 3]))
(fixate!! foo-test (first [1 2 3]) 1)
(fixate!! foo-test "this tests stuff" (first [1 2 3]) 1)
)
