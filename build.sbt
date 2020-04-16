enablePlugins(GitVersioning, LauncherJarPlugin)

name := "hello-uzhttp"

scalaVersion := "2.13.1"

val zioVersion = "1.0.0-RC18-2"

libraryDependencies ++= Seq(
  "org.polynote" %% "uzhttp"       % "0.1.3",

  "dev.zio"      %% "zio-test-sbt" % zioVersion % "test",
)

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")

Global / cancelable := false
