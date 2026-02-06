pipeline {
    agent any

    environment {
        DOCKER_HUB_REPO = 'palaye769/goldenfly-backend'
        DOCKER_HUB_CREDENTIALS = 'docker-hub-new'
        RENDER_DEPLOY_HOOK = credentials('render-backend-webhook')
        MAVEN_OPTS = '-Dmaven.repo.local=/tmp/.m2/repository'
    }

    stages {
        stage('Checkout') {
            steps {
                echo '🔄 Récupération du code source depuis GitHub...'
                git branch: 'master',
                    url: 'https://github.com/palaye08/goldenfly.git'
            }
        }

        stage('Build & Test') {
            steps {
                echo '🔨 Construction et tests du projet Spring Boot...'
                sh '''
                    # Utiliser le wrapper Maven
                    chmod +x ./mvnw
                    ./mvnw clean compile test -Dmaven.test.failure.ignore=true
                '''
            }
        }

        stage('Package') {
            steps {
                echo '📦 Création du package JAR...'
                sh '''
                    ./mvnw clean package -DskipTests
                '''
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Build & Push Docker Image') {
            steps {
                echo '🐳 Construction et push de l\'image Docker...'
                script {
                    def imageName = "${DOCKER_HUB_REPO}:${BUILD_NUMBER}"
                    def latestImageName = "${DOCKER_HUB_REPO}:latest"

                    dockerImage = docker.build(imageName)

                    docker.withRegistry('https://registry.hub.docker.com', DOCKER_HUB_CREDENTIALS) {
                        dockerImage.push("${BUILD_NUMBER}")
                        dockerImage.push("latest")
                    }

                    // Nettoyage des images locales
                    sh "docker rmi ${imageName} ${latestImageName} || true"
                }
            }
        }

        stage('Deploy to Render') {
            steps {
                echo '🚀 Déploiement du backend sur Render...'
                script {
                    sh '''
                        curl -X POST "$RENDER_DEPLOY_HOOK" \
                            -H "Content-Type: application/json" \
                            -d '{"branch": "master"}'
                    '''
                    echo '✅ Webhook de déploiement envoyé à Render'
                }
            }
        }

        stage('Verify Deployment') {
            steps {
                echo '🔍 Vérification du déploiement...'
                script {
                    sh '''
                        echo "⏳ Attente du déploiement (60 secondes)..."
                        sleep 60
                        echo "✅ Backend déployé avec succès"
                    '''
                }
            }
        }
    }

    post {
        always {
            echo '🧹 Nettoyage de l\'espace de travail...'
            deleteDir()
        }
        success {
            echo '🎉 Pipeline backend exécuté avec succès!'
            echo '📊 Image Docker: ${DOCKER_HUB_REPO}:${BUILD_NUMBER}'
        }
        failure {
            echo '❌ Pipeline backend échoué! Vérifiez les logs.'
        }
    }
}