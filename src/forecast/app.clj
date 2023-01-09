(ns forecast.app
  (:require
   [clojure.string :as string]
   [clojure.tools.cli :refer [parse-opts]]
   [table.core :as t]
   [forecast.api :as api])
  (:import
   (java.time OffsetDateTime)
   (java.time.format DateTimeFormatter)))

(def cli-options
  [["-c" "--coordinates COORDINATES" "Coordinates"
    :parse-fn (fn [coords] (zipmap [:latitude :longitude] (map #(Double/parseDouble %) (string/split coords #","))))]
   ["-u" "--units UNITS" "Units"
    :default "F"
    :parse-fn string/upper-case
    :validate [#{"F" "C"} "Units argument must be \"F\" for Fahrenheit or \"C\" for Celsius."]]
   [nil "--hourly" "Display hourly forecast"]])

(defn format-temperature [temperature unit]
  (str temperature "°" unit))

(defn format-period [period date-time-formatter]
  (let [datetime (OffsetDateTime/parse (:startTime period))]
    [(.format datetime date-time-formatter)
     (format-temperature (:temperature period) (:temperatureUnit period))
     (:shortForecast period)]))

(defn print-hourly-forecast [forecast-data]
  (t/table
   (cons ["Time" "Temperature" "Description"]
         (map #(format-period % (DateTimeFormatter/ofPattern "E 'at' h:mm a"))
              forecast-data))))

(defn print-extended-forecast [forecast-data]
  (t/table
   (cons ["Date" "Temperature" "description"]
         (map #(format-period % (DateTimeFormatter/ofPattern "E, LLL d a"))
              forecast-data))))

(defn -main [& args]
  (let [{:keys [options errors]} (parse-opts args cli-options)]
    (if errors
      (do (doseq [error errors]
            (println error))
          (System/exit 1))
      (let [coordinates (or (:coordinates options) (api/get-coordinates-from-ip (api/get-ip-address)))
            office (api/get-nws-office coordinates)
            units (:units options)]
        (if (:hourly options)
          (print-hourly-forecast (api/get-hourly-forecast office units))
          (print-extended-forecast (api/get-extended-forecast office units)))))))
