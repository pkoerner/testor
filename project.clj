(defproject org.clojars.pkoerner/testor "0.1.0-SNAPSHOT"
  :description "A dev library to quickly add tests to the test file while editing rich comments in the sources."
  :url "https://github.com/pkoerner/testor"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :deploy-repositories [["releases"  {:sign-releases false :url "https://repo.clojars.org/"}]
                        ["snapshots" {:sign-releases false :url "https://repo.clojars.org/"}]]
  :dependencies [[org.clojure/clojure "1.10.3"]]
  :repl-options {:init-ns testor.core})
