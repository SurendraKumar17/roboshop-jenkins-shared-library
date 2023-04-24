def call() {
    pipeline {

        agent {
            label 'workstation'
        }

        stages {

            stage('compile/build') {
                steps {
                    echo 'compile'
                }
            }

            stage('Unit Tests') {
                steps {
                    echo 'Unit Tests'
                }
            }

            stage('Quality Control') {
                steps {
                    echo 'Quality Control'
                }
            }

            stage('Upload code to centralized repository') {
                steps {
                    echo 'Upload'
                }
            }

        }

    }

}


