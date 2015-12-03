uniform.project("audition", "au.com.cba.omnia.audition")

val maestroVersion = "2.16.1-20151203024625-d12c69e-SNAPSHOT"

strictDependencySettings
uniformDependencySettings
uniformAssemblySettings
humbugSettings

libraryDependencies :=
  depend.hadoopClasspath ++ depend.hadoop() ++ depend.testing() ++
  depend.omnia("maestro",      maestroVersion)

humbugThriftSourceFolder in Compile <<= (sourceDirectory) { _ / "main" / "thrift" / "humbug" }
updateOptions := updateOptions.value.withCachedResolution(true)
publishArtifact in Test := true
uniform.docSettings("https://github.com/CommBank/audition")
uniform.ghsettings

