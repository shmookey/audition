resolvers += Resolver.url("commbank-releases-ivy", new URL("http://commbank.artifactoryonline.com/commbank/ext-releases-local-ivy"))(Patterns("[organization]/[module]_[scalaVersion]_[sbtVersion]/[revision]/[artifact](-[classifier])-[revision].[ext]"))

val uniformVersion = "1.4.0-20151122210042-f94f9b6"
val humbugVersion  = "0.6.1-20151008040202-1f0ccb9"

addSbtPlugin("au.com.cba.omnia" % "uniform-core"       % uniformVersion)
addSbtPlugin("au.com.cba.omnia" % "uniform-dependency" % uniformVersion)
addSbtPlugin("au.com.cba.omnia" % "humbug-plugin"      % humbugVersion)
