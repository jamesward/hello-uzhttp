import com.typesafe.sbt.packager.docker.DockerPermissionStrategy

enablePlugins(GitVersioning, GraalVMNativeImagePlugin)

name := "hello-uzhttp"

scalaVersion := "2.13.4"

val zioVersion = "1.0.2"

libraryDependencies ++= Seq(
  "org.polynote" %% "uzhttp"       % "0.2.6",

  "dev.zio"      %% "zio-test-sbt" % zioVersion % Test,
)

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-explaintypes",
  "-feature",
  "-Wconf:any:error",
  "-Wunused",
  "-Wvalue-discard",
)


testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")

Global / cancelable := false

javacOptions ++= Seq("-source", "11", "-target", "11")

scalacOptions += "-target:jvm-11"

initialize := {
  val _ = initialize.value
  val javaVersion = sys.props("java.specification.version")
  if (javaVersion != "11")
    sys.error("Java 11 is required for this project. Found " + javaVersion + " instead")
}

publishArtifact in (Compile, packageDoc) := false

publishArtifact in packageDoc := false

sources in (Compile,doc) := Seq.empty

// if this is specified, graalvm runs inside docker, otherwise it uses an PATH'd native-image
//graalVMNativeImageGraalVersion := Some("20.0.0-java11")

graalVMNativeImageOptions ++= Seq(
  "--verbose",
  "--no-server",
  "--no-fallback",
  "--static",
  "-H:+ReportExceptionStackTraces",
  "-H:+TraceClassInitialization",
  "-H:+PrintClassInitialization",
  "-H:UseMuslC=../../bundle/",
  "--initialize-at-build-time=scala.runtime.Statics$VM",
)