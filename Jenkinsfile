#!groovy

stage 'Build'
node {
  checkout scm

  // Build and test
  test()
  // Make a jar
  sh "./gradlew jar"

  // Archive test results
  step([$class: 'JUnitResultArchiver', testResults: '**/build/test-results/TEST-*.xml'])
  // Archive PMD results
  step([$class: 'PmdPublisher', pattern: '**/pmd/*.xml'])
  // Archive artifacts
  archive includes: '**/build/libs/*.jar'
}

def test() {
  // Gradle does not yet support ignoring test failures, bash to the rescue
  sh "./gradlew clean check || echo 'There were test failures'"
}
