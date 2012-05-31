sbtPlugin := true

name := "sbt-dependency-graph"

organization := "tspaulino"

version := "0.6.0"

homepage := Some(url("http://github.com/tspaulino/sbt-dependency-graph"))

licenses in GlobalScope += "Apache License 2.0" -> url("https://github.com/tspaulino/sbt-dependency-graph/raw/master/LICENSE")

(LsKeys.tags in LsKeys.lsync) := Seq("dependency", "graph", "sbt-plugin", "sbt")

(LsKeys.docsUrl in LsKeys.lsync) <<= homepage

(description in LsKeys.lsync) :=
  "An sbt plugin to visualize dependencies of your build."
