# Weather Forecast

This is a small program to retrieve the weather forecast from the [National Weather Service](https://www.weather.gov/documentation/services-web-api). The main reason I wrote it was to play around with Clojure and Babashka.

You can fetch the forecast by running the command `bb -m forecast.app` (assuming you have [Babashka](https://babashka.org/) installed). You can use the following options:

* `-c` or `--coordinates` : X and Y coordinates of a specific location. Must be separated by a comma and without whitespace. If you don't use this option, then the program will find your location from your IPv4 address.
* `-u` or `--units` : Temperature units. Must be F for Fahrenheit or C for Celsius.
* `--hourly` : Fetch the hourly forecast instead of the weekly forecast.
