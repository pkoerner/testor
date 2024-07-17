(ns testor.core
  (:require [clojure.string :as str]
            [clojure.pprint :as pp]))

(defmacro gen-test [form]
  `(~'deftest ~(symbol (str (name (first form)) "-test-" (name (gensym ""))))
     (~'testing "FIXME: describe something"
       (~'is (~'= ~form ~(eval form))))))

(macroexpand-1 '(gen-test (first [1 2 3])))

;; stolen from clojure.tools.namespace.move
(defn- ns-file-name [sym]
  (str (-> (name sym)
           (str/replace "-" "_")
           (str/replace "." java.io.File/separator))
       "_test"
       ".clj"))

(defn get-test-file []
  (clojure.java.io/file "test/" (ns-file-name (ns-name *ns*))))

(defn add-test! [macroexpanded-test]
  (let [file (get-test-file)]
    (when (.exists file)
      (spit file (str \newline (with-out-str (pp/pprint macroexpanded-test))) :append true))))
(.exists (get-test-file))

(add-test! (macroexpand-1 '(gen-test (first [1 2 3]))))

(defmacro fixate!! 
  ([form] (add-test! (macroexpand-1 `(gen-test ~form))))
  ([form expected] (add-test! (macroexpand-1 `(gen-test ~form ~expected)))))

(fixate!! (first [1 2 3]))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))
