// Runs shell commands on Linux/macOS agents and batch commands on Windows agents
def run(String cmd) {
    if (isUnix()) {
        sh cmd
    } else {
        bat cmd
    }
}

def dockerAvailable = false

pipeline {
    agent any

    // Java 17+ and Maven must be on the agent's PATH

    environment {
        IMAGE_NAME = 'employee-management'
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        CONTAINER_NAME = 'employee-management'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
    }

    triggers {
        // Check GitHub for new commits every 5 minutes
        pollSCM('H/5 * * * *')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                run 'mvn -B clean compile'
            }
        }

        stage('Test') {
            steps {
                run 'mvn -B test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                run 'mvn -B package -DskipTests'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Check Docker') {
            steps {
                script {
                    def status = isUnix()
                            ? sh(script: 'docker info', returnStatus: true)
                            : bat(script: 'docker info', returnStatus: true)
                    dockerAvailable = (status == 0)
                    if (!dockerAvailable) {
                        echo 'Docker is not available on this agent - skipping Docker Build and Deploy.'
                    }
                }
            }
        }

        stage('Docker Build') {
            when { expression { dockerAvailable } }
            steps {
                run "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} -t ${IMAGE_NAME}:latest ."
            }
        }

        stage('Deploy') {
            when { expression { dockerAvailable } }
            steps {
                script {
                    if (isUnix()) {
                        sh "docker rm -f ${CONTAINER_NAME} || true"
                    } else {
                        bat "docker rm -f ${CONTAINER_NAME} || exit /b 0"
                    }
                }
                run "docker run -d --name ${CONTAINER_NAME} -p 8080:8080 ${IMAGE_NAME}:${IMAGE_TAG}"
            }
        }
    }

    post {
        success {
            echo "Pipeline succeeded for build ${IMAGE_TAG}"
        }
        failure {
            echo 'Pipeline failed.'
        }
    }
}
