# OSGi Bundle Tool

Tool to extract and install OSGi bundle information, similar to the previous com.cqblueprints.cqdependencies\5.6.1\cqdependencies-5.6.1.pom file
Operates on a directory structure directly from crx-quickstart\launchpad\felix\ or a copy of this structure.
Should be a "clean" version of AEM 6.1.0 with no extra bundles install
    
The base dir below should point to the root folder of the structure.
For example:
F:\AEM61V2
  bundle0
  bundle1
  ...
  bundle442
     
##The program is capable of:
1. Generate a pom.xml file that can be used in the dependencyManagement section of the parent pom file:
   <dependency>
     <groupId>com.hm.cms.cqblueprints</groupId>
     <artifactId>cqdependencies</artifactId>
     <version>6.1.0</version>
     <type>pom</type>
     <scope>import</scope>
   </dependency>

2. Extract pom.xml files from installed bundles and generate local or remote install script for Maven repo
     
##Additional:
  com.day.cq.parent version 42 must be installed as a copy of version 40
  com.adobe.granite.parent version 33, 50, 52 and 54 must be installed as a copy of version 32
The reason is that these versions are not available in any public repositories and can not be extracted from
information in the installed OSGi bundles.