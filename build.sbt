import com.typesafe.sbt.packager.docker.{Cmd, ExecCmd}

enablePlugins(GitVersioning, LauncherJarPlugin, DockerPlugin)

name := "hello-uzhttp"

scalaVersion := "2.13.1"

lazy val uzhttp = RootProject(uri("https://github.com/jamesward/uzhttp.git"))

lazy val root = (project in file(".")).dependsOn(uzhttp)

// todo: use same version as uzhttp?
val zioVersion = "1.0.0-RC18-2"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio-test"          % zioVersion % "test",
  "dev.zio" %% "zio-test-sbt"      % zioVersion % "test",
)

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")

Global / cancelable := false

dockerBaseImage := "gcr.io/distroless/java:8"

dockerUpdateLatest := true

dockerAutoremoveMultiStageIntermediateImages := false

dockerCommands := Seq(
  Cmd("FROM", "gcr.io/distroless/java:8"),
  Cmd("WORKDIR", "/opt/docker"),
  Cmd("COPY", "1/opt", "/opt"),
  Cmd("COPY", "2/opt", "/opt"),
  ExecCmd("CMD", s"/opt/docker/lib/${(artifactPath in packageJavaLauncherJar).value.getName}")
)
