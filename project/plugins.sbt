resolvers += Resolver.url("hmrc-sbt-plugin-releases", url("https://dl.bintray.com/hmrc/sbt-plugin-releases"))(Resolver.ivyStylePatterns)
resolvers += "HMRC Releases" at "https://dl.bintray.com/hmrc/releases"

resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("uk.gov.hmrc" % "sbt-auto-build" % "1.13.0" )

addSbtPlugin("uk.gov.hmrc" % "sbt-git-versioning" % "1.15.0" )

addSbtPlugin("uk.gov.hmrc" % "sbt-distributables" % "1.1.0" )

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.19" )

addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.8.5")

addSbtPlugin("uk.gov.hmrc" % "sbt-bobby" % "0.32.0")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.0")

addSbtPlugin("uk.gov.hmrc" % "sbt-artifactory" % "0.13.0")
