#!groovy

stage 'Build'
node {
  //  step([$class: 'GitHubSetCommitStatusBuilder', statusMessage: [content: 'Building commit...']])

  checkout scm

  // Build and test
  test()

  // Archive test results
  step([$class: 'JUnitResultArchiver', testResults: '**/build/test-results/TEST-*.xml'])
  // Archive PMD results
  step([$class: 'PmdPublisher', canComputeNew: false,
        defaultEncoding: '', healthy: '',
        pattern: '**/pmd/*.xml', unHealthy: ''])
  //step([$class: 'PmdPublisher', pattern: '**/pmd/*.xml'])

  step([$class: 'GitHubCommitNotifier', resultOnFailure: 'FAILURE',
        statusMessage: [content: 'Build finished']])
}

stage 'QA'
node {
  // Run mutation testing
  sh "./gradlew pitest"

  //step([$class: 'PitPublisher'])
}

stage 'Package'
node {
  sh "./gradlew jar"
  // Archive artifacts
  archive includes: '**/build/libs/*.jar'
}

def test() {
  // Gradle does not yet support ignoring test failures, bash to the rescue
  sh "./gradlew clean check || echo 'There were test failures'"
}


// step([$class: 'GitHubCommitNotifier', resultOnFailure: 'FAILURE'])
