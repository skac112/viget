enablePlugins(ScalaJSPlugin)
name := "viget"
scalaVersion := "2.12.4" // or any other Scala version >= 2.10.2
//libraryDependencies += "be.doeraene" %%% "scalajs-jquery" % "0.9.2"
libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.1"
libraryDependencies += "com.github.lukajcb" %%% "rxscala-js" % "0.15.0"
libraryDependencies += "com.lihaoyi" %%% "scalatags" % "0.6.7"
libraryDependencies += "com.chuusai" %%% "shapeless" % "2.3.3"
jsDependencies += "org.webjars.npm" % "rxjs" % "5.4.0" / "bundles/Rx.min.js" commonJSName "Rx"
// This is an application with a main method
scalaJSUseMainModuleInitializer := true
//scalaJSLinkerConfig ~= { _.withOutputMode(OutputMode.ECMAScript2015) }
scalacOptions += "-P:scalajs:sjsDefinedByDefault"
