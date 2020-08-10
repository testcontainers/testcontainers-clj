(ns user
  (:require
   [clojure.spec.alpha :as s]
   [expound.alpha :as ea]
   [kaocha.repl]))

(try
  ;; Attempt to set *explain-out*, assuming that we're inside of
  ;; a binding context...
  (set! s/*explain-out* ea/printer)

  (catch IllegalStateException _
    ;; ...if not, just alter the root binding.
    (alter-var-root #'s/*explain-out* (constantly ea/printer))))
