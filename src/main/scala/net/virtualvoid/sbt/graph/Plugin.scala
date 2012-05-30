/*
 * Copyright 2011, 2012 Johannes Rudolph
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package net.virtualvoid.sbt.graph

import sbt._
import Keys._

object Plugin extends sbt.Plugin {
  val dependencyGraphMLFile = SettingKey[File]("dependency-graph-ml-file",
    "The location the graphml file should be generated at")
  val dependencyGraphML = TaskKey[File]("dependency-graph-ml",
    "Creates a graphml file containing the dependency-graph for a project")
  val asciiGraph = TaskKey[String]("dependency-graph-string",
    "Returns a string containing the ascii representation of the dependency graph for a project")
  val dependencyGraph = TaskKey[Unit]("dependency-graph",
    "Prints the ascii graph to the console")
  val ivyReportFunction = TaskKey[String => File]("ivy-report-function",
    "A function which returns the file containing the ivy report from the ivy cache for a given configuration")
  val ivyReport = TaskKey[File]("ivy-report",
    "A task which returns the location of the ivy report file for a given configuration (default `compile`).")
  val dependencyGraphHTMLFile = SettingKey[File]("dependency-graph-html-file",
  	"The location the html file should be generated at")
  val dependencyGraphHTML = TaskKey[File]("dependency-graph-html",
  	"A task which creates a HTML file with ascii graph information.")


  def graphSettings = seq(
    ivyReportFunction <<= (projectID, ivyModule, appConfiguration) map { (projectID, ivyModule, config) =>
      val home = config.provider.scalaProvider.launcher.ivyHome
      (c: String) => file("%s/cache/%s-%s-%s.xml" format (home, projectID.organization, crossName(ivyModule), c))
    }
  ) ++ Seq(Compile, Test, Runtime, Provided, Optional).flatMap(ivyReportForConfig)

  def ivyReportForConfig(config: Configuration) = inConfig(config)(seq(
    ivyReport <<= ivyReportFunction map (_(config.toString)) dependsOn(update),
    asciiGraph <<= asciiGraphTask,
    dependencyGraph <<= printAsciiGraphTask,
    dependencyGraphMLFile <<= target / "dependencies-%s.graphml".format(config.toString),
    dependencyGraphML <<= dependencyGraphMLTask,
    dependencyGraphHTMLFile <<= target / "dependencies-%s.html".format(config.toString),
    dependencyGraphHTML <<= dependencyGraphHTMLTask
  ))

  def asciiGraphTask = (ivyReport) map { report =>
    IvyGraphMLDependencies.ascii(report.getAbsolutePath)
  }

  def printAsciiGraphTask =
    (streams, asciiGraph) map (_.log.info(_))

  def dependencyGraphMLTask =
    (ivyReport, dependencyGraphMLFile, streams) map { (report, resultFile, streams) =>
      IvyGraphMLDependencies.transform(report.getAbsolutePath, resultFile.getAbsolutePath)
      streams.log.info("Wrote dependency graph to '%s'" format resultFile)
      resultFile
    }

  def dependencyGraphHTMLTask =
    (ivyReport, dependencyGraphHTMLFile, streams) map { (report, resultFile, streams) =>
      IvyGraphMLDependencies.transformToHTML(report.getAbsolutePath, resultFile.getAbsolutePath)
      streams.log.info("Wrote dependency graph html to '%s'" format resultFile)
      resultFile
    }

  def crossName(ivyModule: IvySbt#Module) =
    ivyModule.moduleSettings match {
      case ic: InlineConfiguration => ic.module.name
      case _ =>
        throw new IllegalStateException("sbt-dependency-graph plugin currently only supports InlineConfiguration of ivy settings (the default in sbt)")
    }
}