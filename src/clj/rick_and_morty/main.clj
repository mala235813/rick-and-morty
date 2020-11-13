(ns rick-and-morty.main
  (:require [io.pedestal.http :as http]))

(defn hello
  [request]
  {:status 200
   :body "Rick and morty app!"})

(defn echo
  [request]
  {:status 200
   :body "Should echo request here"})

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
                     ::http/routes routes}
         server (http/create-server server-map)]
     (http/start server)
     (reset! instance server))))

(defn stop
  []
  (when (some? (deref instance))
    (http/stop (deref instance))
    (reset! instance nil)))

(comment
  (start)

  (stop)

  )
(defn -main
  [& args]
  (start true))
