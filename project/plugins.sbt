resolvers += "less is" at "http://repo.lessis.me"

addSbtPlugin("me.lessis" % "ls-sbt" % "0.1.1")

resolvers += "Coda Hale's Repo" at "http://repo.codahale.com"

resolvers += Resolver.url("scalasbt", new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns)

addSbtPlugin("net.virtual-void" % "sbt-cross-building" % "0.6.0")

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.0.0")
