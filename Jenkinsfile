pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'goldenfly-backend'
        RENDER_SERVICE_NAME = 'goldenfly-backend'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/votre-username/goldenfly-backend.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("${DOCKER_IMAGE}:${BUILD_NUMBER}")
                    docker.build("${DOCKER_IMAGE}:latest")
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                script {
                    docker.withRegistry('https://registry.hub.docker.com', 'docker-hub-credentials') {
                        docker.image("${DOCKER_IMAGE}:${BUILD_NUMBER}").push()
                        docker.image("${DOCKER_IMAGE}:latest").push()
                    }
                }
            }
        }

        stage('Deploy to Render') {
            steps {
                script {
                    sh '''
                        curl -X POST https://api.render.com/v1/services/${RENDER_SERVICE_ID}/deploys \
                        -H "Authorization: Bearer ${RENDER_API_KEY}" \
                        -H "Content-Type: application/json" \
                        -d '{"clearCache": false}'
                    '''
                }
            }
        }
    }

    post {
        success {
            echo 'Déploiement réussi sur Render!'
        }
        failure {
            echo 'Le déploiement a échoué.'
        }
    }
}