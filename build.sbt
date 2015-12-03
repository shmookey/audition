uniform.project("audition", "au.com.cba.omnia.audition")

val maestroVersion = "2.16.1-20151125012349-278db85"

uniformDependencySettings
strictDependencySettings
humbugSettings

libraryDependencies :=
  depend.hadoopClasspath ++ depend.hadoop() ++ depend.testing() ++
  depend.omnia("maestro-core", maestroVersion) ++
  depend.omnia("maestro-scalding", maestroVersion)

humbugThriftSourceFolder in Compile <<= (sourceDirectory) { _ / "main" / "thrift" / "humbug" }
updateOptions := updateOptions.value.withCachedResolution(true)
publishArtifact in Test := true
uniform.docSettings("https://github.com/CommBank/audition")
uniform.ghsettings
