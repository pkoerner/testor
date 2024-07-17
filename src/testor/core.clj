(ns testor.core
  (:require [clojure.string :as str]
            [clojure.pprint :as pp]))

(defn- gen-test 
  ([form] (gen-test form (eval form)))
  ([form expected]
  `(~'deftest ~(symbol (str (name (first form)) "-test-" (quot (System/currentTimeMillis) 1000) "-" (name (gensym ""))))
     (~'testing "this was deemed correct during development"
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

(defn- add-test! [macroexpanded-test]
  (let [file (get-test-file)]
    (when (.exists file)
      (spit file (str \newline (with-out-str (pp/pprint macroexpanded-test))) :append true))))

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
  ([form] (add-test! (gen-test form)))
  ([form expected] (add-test! (gen-test form expected))))

(comment 
(fixate!! (first [1 2 3]))
(fixate!! (first [1 2 3]) (+ 1 1)))
