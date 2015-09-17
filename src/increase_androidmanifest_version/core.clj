;; In ns below, notice that "gen-class" was removed
(ns increase-androidmanifest-version.core
  ;; We haven't gone over require but we will.
  (:require [clojure.string :as s])
  (:gen-class))

(def filename "AndroidManifest.xml")

(defn str->int
  [str]
  (Integer. str))

(defn increase-versionCode
  "update versionCode integer. \"1\" -> \"2\""
  [codeStr]
  (str (inc (str->int codeStr))))

(defn increase-versionName
  "increate last version integer. \"1.1.1.1\" -> \"1.1.1.2\""
  [codeNameStr]
  (let [last-subversion-str (last (clojure.string/split codeNameStr #"\."))
        last-subversion-count (count last-subversion-str)
        unhandle-version-str (subs codeNameStr 0
                                   (- (count codeNameStr) last-subversion-count))]
    (clojure.string/join (conj []
                               unhandle-version-str
                               (str (inc (str->int last-subversion-str)))))))

(def conversions {"android:versionCode" increase-versionCode
                  "android:versionName" increase-versionName})

(defn parse
  "read files divider as line"
  [string]
  (map #(s/split % #"=")
       (s/split string #"\n")))

(def filelines (slurp filename))

(defn need-to-process
  [line]
  (let [pair (s/split line #"=")]
    (and
     (= (count pair) 2)
     (or
      (re-find #"android:versionCode" (first pair))
      (re-find #"android:versionName" (first pair))))))

(def version-describation "version ")

(defn process-line
  "take argument like \"android:versionCode=\"8\">\""
  [line]
  (let [line-pair (s/split line #"=")
        processor (get conversions (s/trim (first line-pair)))
        replacements (s/split (second line-pair) #"\"")
        str-to-process (second replacements)]
    (s/join (conj []
                  (first line-pair)
                  "="
                  "\""
                  (let [new-str (processor  str-to-process)]
                    (def version-describation (str version-describation new-str " "))
;                    (print version-describation)
                    new-str)
                  "\""
                  (if (= (count replacements) 2)
                    ""
                    (last replacements))))))

(defn print-processed-lines
  [lines]
  (reduce (; fun that find line to process and return the processed result
           fn [processed-lines line]
              (let [pair (clojure.string/split line #"=")]
                (if (need-to-process line)
                  (conj processed-lines (process-line line))
                  processed-lines)))
          []
          lines))

(defn process-file
  "process file increase number, return a processed string content"
  [filename]
  (s/join "\n"
          (let [lines (s/split-lines filelines)]
            (reduce (fn [final-lines line]
                      (let [pair (s/split line #"=")]
                        (if (need-to-process line)
                          (conj final-lines (process-line line))
                          (conj final-lines line))))
                    []
                    lines))))

;; (spit "tmp" (s/join "\n"
;;                     (print-processed-lines (s/split-lines filelines))))

;; (spit "tmp2" (process-file filename))



(defn -main
  "increte AndroidManifest.xml in same directory. Print the comment"
  [& args]
  (spit filename (process-file filename))
  (println version-describation))




