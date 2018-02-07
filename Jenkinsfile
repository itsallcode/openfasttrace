node {
   git url: 'https://github.com/itsallcode/openfasttrace.git'
   def mvnHome = tool 'M3'
   sh "${mvnHome}/bin/mvn -B verify"
   step([$class: 'ArtifactArchiver', artifacts: '**/target/*.jar', fingerprint: true])
   step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])
}
