# sahaj

A Clojure library designed to execute stock orders

## Installation

This assumes leiningen is installed and if not please refer to https://leiningen.org/ .

* Clone the repo
* Go to source directory
* Run `lein uberjar`

## Minimum requirements

* JDK 7 or later
* Clojure 1.9

## Usage

`java -jar target/sahaj-0.1.0-SNAPSHOT-standalone.jar path/to/input path/to/output`

Example

`java -jar target/sahaj-0.1.0-SNAPSHOT-standalone.jar sample.csv sample-out.csv`

## Docs

Extensive docs available at [doc](doc/)

* `cd doc/`
* `python3 -m http.server 9001`
* Point the browser to `http://127.0.0.1:9001`

## Specs packed

This package includes specs for the functions. Refer [specs](doc/specs.md)

Example

```
sahaj.core/cumulative-sum
 [orders]
  Cumulative sum of the quantity of orders

Spec:

arguments  : (s/cat
              :orders (s/coll-of :sahaj.core/order))
returns    : (s/coll-of number?)
```

## License

Copyright © 2018 Karthikeyan S

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
