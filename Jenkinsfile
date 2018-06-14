pipeline {
  agent any
  stages {
    stage('build') {
      steps {
        sh 'mvn package -Dmaven.test.skip=true'
      }
    }
  }
}