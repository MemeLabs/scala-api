package example

import java.io.{FileInputStream, InputStreamReader}
import java.util.Properties

import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import sbt.Keys._
import sbt._

/**
  *
  */
object Build {

  lazy val defaultSettings: Seq[Def.Setting[_]] = Def.settings(
    organization := "example.organization",
    organizationName := "Organization",
    organizationHomepage := Some(url("https://www.organization.com")),
    homepage := Some(url("https://www.github.com")),
    startYear := Some(2017),
    licenses := Seq("Apache-2.0" -> url("https://opensource.org/licenses/Apache-2.0")),
    scalacOptions ++= Seq(
      "-deprecation",
      "-encoding", "UTF-8",
      "-unchecked",
      "-Xlint",
      "-Ywarn-dead-code"
    ),
    javacOptions ++= Seq(
      "-encoding", "UTF-8"
    ),
    testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v"),
    Dependencies.Versions,
    Formatting.formatSettings,
    ScalariformKeys.autoformat in Compile := false,
    SbtScalariform.autoImport.scalariformAutoformat in Compile := false,
    shellPrompt := { s => Project.extract(s).currentProject.id + " > " },
    resolvers += Classpaths.typesafeReleases,
    resolvers += Resolver.sonatypeRepo("releases"),
    resolvers += Resolver.url("spray repo", url("http://repo.spray.io")),
    resolvers += Resolver.url("jitpack", url("https://jitpack.io"))
  )

  lazy val buildSettings: Seq[Def.Setting[_]] = Dependencies.Versions ++ Seq(
    organization := "com.osinc.lp",
    version := "0.0.0")


  def loadSystemProperties(fileName: String): Unit = {
    import scala.collection.JavaConverters._
    val file = new File(fileName)
    if (file.exists()) {
      println("Loading system properties from file `" + fileName + "`")
      val in = new InputStreamReader(new FileInputStream(file), "UTF-8")
      val props = new Properties
      props.load(in)
      in.close()
      sys.props ++ props.asScala
    }
  }

}
