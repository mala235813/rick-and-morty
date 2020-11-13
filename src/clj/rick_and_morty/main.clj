(ns rick-and-morty.main
  (:require [clojure.pprint :as pp]
            [io.pedestal.http :as http]))

(defn hello
  [request]
  {:status 200
   :body "Rick and morty app!"})

(def echo
  {:name :echo
   :enter (fn [context]
          (let [response {:status 200
                          :headers {"Content-Type" "text/plain"}
                          :body (with-out-str (pp/pprint context))}]
            (assoc context :response response)))})

(def routes #{["/" :get hello :route-name ::root]
              ["/echo" :any echo :route-name ::echo]})

(defonce instance (atom nil))

(defn start
  ([] (start false))
  ([do-join]
   (let [server-map {::http/type :jetty
                     ::http/host "0.0.0.0"
                     ::http/port 80
                     ::http/join? do-join
                     ::http/routes routes
                     ::http/resource-path "/public"}
         server (http/create-server server-map)]
     (http/start server)
     (reset! instance server))))

(defn stop
  []
  (when (some? (deref instance))
    (http/stop (deref instance))
    (reset! instance nil)))

(defn restart
  ([] (restart false))
  ([do-join]
   (stop)
   (start do-join)))

(comment
  (start)

  (stop)

  (restart)

  )
(defn -main
  [& args]
  (start true))
