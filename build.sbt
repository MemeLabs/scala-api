import example.{Dependencies, Build}

val workaround: Unit = sys.props("packaging.type") = "jar"

lazy val root = Project(
  id = "root",
  base = file(".")
)
  .enablePlugins(ScalaUnidocPlugin)
  .disablePlugins(RevolverPlugin)
  .aggregate(
    core,
    coreTest
  )

lazy val coreBuildSettings = Seq(
  fork := true,
  packMain := Map("application" -> "com.app.api.Bootstrap"),
  packJvmOpts := Map("application" ->
  List("-Dlogback.configurationFile=/deploy/resources/logback.xml",
    "-XX:+UnlockExperimentalVMOptions",
    "-XX:+UseCGroupMemoryLimitForHeap",
    "-XX:+UnlockExperimentalVMOptions"))
)

lazy val coreJobsSettings = Seq(
  fork := true,
  packMain := Map("application" -> "com.app.jobs.Bootstrap"),
  packJvmOpts := Map("application" ->
  List("-Dlogback.configurationFile=/deploy/resources/logback.xml",
    "-XX:+UnlockExperimentalVMOptions",
    "-XX:+UseCGroupMemoryLimitForHeap",
    "-XX:+UnlockExperimentalVMOptions"))
)

lazy val core = project("core")
  .settings(Dependencies.core)
  .enablePlugins(RevolverPlugin)

lazy val coreAPI = project("core-api")
  .dependsOn(core)
  .settings(coreBuildSettings)
  .enablePlugins(RevolverPlugin)

lazy val coreJobs = project("core-jobs")
  .dependsOn(core)
  .settings(coreJobsSettings)
  .enablePlugins(RevolverPlugin)

lazy val coreTest = project("core-test")
  .settings(Dependencies.coreTest)
  .dependsOn(core)
  .dependsOn(coreAPI)

def project(name: String) =
  Project(id = name, base = file(name))
    .settings(Build.buildSettings)
    .settings(Build.defaultSettings)