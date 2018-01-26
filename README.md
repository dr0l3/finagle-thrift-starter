## Intellij

Do this if there are read squiglies

 1) Go to settings page for SBT at Settings -> Build, Execution, Deployment -> Build Tools -> SBT.
 2) In the launcher section, choose Custom and point to the SBT launcher installed in the OS. In Ubuntu, the default location is /usr/share/sbt-launcher-packaging/bin/sbt-launcher.jar

If that doesnt work:

I had the same problem. I solved by choosing again at the moment of opening the project /usr/share/sbt-launcher-packaging/bin/sbt-launcher.jar in "Import Project from SBT" -> Global SBT settings.