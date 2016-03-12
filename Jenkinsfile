#!groovy

stage 'Build'
node {
  checkout scm

  test()

  // Archive test results
  step([$class: 'JUnitResultArchiver', testResults: '**/build/test-results/TEST-*.xml'])
}

def test() {
  // Gradle does not yet support ignoring test failures, bash to the rescue
  sh "./gradlew clean check || echo 'There were test failures'"
}
