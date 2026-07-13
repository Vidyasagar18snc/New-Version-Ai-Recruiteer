@Library('main_shared') _

pipeline {
    agent any

    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(daysToKeepStr: '2'))
    }

    environment {
        GIT_URL = 'https://github.com/shivashankarbukka402/AiRecruiter.git'
        GIT_BRANCH = 'backend-deploy'
        GIT_CREDENTIALS = 'github_token'
        IMAGE_NAME = 'shiva9828/shankar-backend'
        IMAGE_TAG = 'latest'
        DOCKERFILE_BASE = 'Dockerfile'
        DOCKER_REGISTRY_CRED_ID = 'docker_token'
        SONAR_HOST_URL='http://13.232.171.47:9000'
        CREDS_ID = 'k8s_config'  // Replace with your credentials ID
        API_URL = 'https://192.168.49.2:8443'  // Replace with the API URL of your cluster
        CONTEXT_NAME = 'minikube' // Replace with the context name
        CLUSTER_NAME = 'minikube' // Replace with the cluster name
        USER_NAME = 'minikube' // Replace with the user name
    }

    stages {
        stage('CHECKOUT') {
            steps {
                script {
                    gitCheckout(env.GIT_BRANCH, env.GIT_CREDENTIALS, env.GIT_URL)
                }
            }
        }

        stage('Sonar Code Quality check') {
            steps {
                script {
                    dir('./backend') {
                        withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
                            sh """
                                sonar-scanner \
                                  -Dsonar.projectKey=backend \
                                  -Dsonar.sources=. \
                                  -Dsonar.host.url=${env.SONAR_HOST_URL} \
                                  -Dsonar.token=${SONAR_TOKEN}
                            """
                        }
                    }
                }
            }
        }
        stage('Docker Build and Push') {
            steps {
                script {
                    sh '''
                        ls -lrt
                    '''
                    dir('./backend') {
                        dockerImage = docker.build("${IMAGE_NAME}:${IMAGE_TAG}", "-f ${DOCKERFILE_BASE} .")
                        docker.withRegistry('', "${DOCKER_REGISTRY_CRED_ID}") {
                            dockerImage.push()
                        }
                    }
                }
            }
        }

        stage('Trivy Scan') {
            steps {
                sh '''
                    trivy image \
                    --severity HIGH,CRITICAL \
                    --format template \
                    --template '@/home/ubuntu/trivy/trivy_html.tpl' \
                    --output 'trivy_backend.html' ${IMAGE_NAME}:${IMAGE_TAG}
                '''
            }
        }

        stage('Deploy K8S') {
            steps {

                   script {
                        // connectToK8s(env.API_URL, env.CREDS_ID, env.CONTEXT_NAME, env.CLUSTER_NAME, env.USER_NAME)
                        withCredentials([usernamePassword(credentialsId: 'argocd-cli-creds', usernameVariable: 'ARGO_USER', passwordVariable: 'ARGO_PASS')]) {
                            argoAppSyncWithCLI(
                              appName: 'backend',
                              argocdHost: '3.110.186.242:31556',
                              username: env.ARGO_USER,
                              password: env.ARGO_PASS,
                              insecure: true
                            )
                          }
                   }
            }
        }

    }

    post {
        always {
            publishHTML (target : [allowMissing: false,
                    alwaysLinkToLastBuild: false,
                    keepAll: true,
                    reportDir: '',
                    reportFiles: 'trivy_backend.html',
                    reportName: 'Backend Trivy',
                    reportTitles: 'The Report'])
            cleanWs()
        }
    }
}

