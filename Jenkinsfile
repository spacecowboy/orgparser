#!groovy


node {
  // Mark the code checkout 'stage'....
  stage 'Checkout'

  // Get some code from a GitHub repository
  git url: 'https://github.com/spacecowboy/orgparser.git'

  // Mark the code build 'stage'....
  stage 'Build'
  sh "./gradlew clean assemble"

  stage 'Test'
  // Gradle does not yet support ignoring test failures, bash to the rescue
  sh "./gradlew check || echo 'There were test failures'"

  // Archive test results
  step([$class: 'JUnitResultArchiver', testResults: '**/build/test-results/TEST-*.xml'])
}