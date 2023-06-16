lazy val root = project.in(file("."))
  .settings(
    // skip in publish := true
  )
  // .aggregate(protobuf, client, server)
  .aggregate(protobuf, server)

ThisBuild / scalaVersion := "3.3.0"

ThisBuild / scalacOptions ++=
  Seq(
    "-explain",
  ) // ++ Seq("-rewrite", "-indent")

addCommandAlias("cd", "project")
addCommandAlias("c", "compile")
addCommandAlias("r", "run")

addCommandAlias(
  "styleFix",
  "scalafmtSbt; scalafmtAll",
)

addCommandAlias(
  "ud",
  "reload plugins; update; reload return",
)

val protobuf =
  project
    .in(file("protobuf"))
    .enablePlugins(Fs2Grpc)

// lazy val client =
//   project
//     .in(file("client"))
//     .settings(
//       libraryDependencies ++= List(
//         "io.grpc" % "grpc-netty" % "1.11.0"
//       )
//     )
//     .dependsOn(protobuf)

lazy val server =
  project
    .in(file("server"))
    .settings(
      libraryDependencies ++= List(
       "io.grpc" % "grpc-netty-shaded" % scalapb.compiler.Version.grpcJavaVersion,
        "io.grpc" % "grpc-services" % "1.56.0",
        // "io.grpc" % "grpc-netty" % "1.56.0",
      )
    )
    .dependsOn(protobuf)
