pipeline {
  agent {
    kubernetes {
      yaml """
apiVersion: v1
kind: Pod
metadata:
  labels:
    jenkins: maven-docker
spec:
  containers:
  - name: maven
    image: maven:3.8.5-openjdk-17
    command:
    - cat
    tty: true
  - name: docker
    image: docker:20.10.16-dind
    securityContext:
      privileged: true
    env:
    - name: DOCKER_HOST
      value: tcp://localhost:2375
    - name: DOCKER_DRIVER
      value: overlay2
    - name: DOCKER_TLS_CERTDIR
      value: ""
  - name: kubectl
    image: bitnami/kubectl:latest
    command:
    - cat
    tty: true
"""
    }
  }

  environment {
    KUBECONFIG_B64 = credentials('kubeconfig') 
  }

  stages {
    stage('Maven Build') {
      steps {
        container('maven') {
          sh 'mvn clean install -DskipTests'
        }
      }
    }

    stage('Docker Build') {
      steps {
        container('docker') {
          withCredentials([usernamePassword(credentialsId: 'dockerhub', passwordVariable: 'DOCKERHUB_PASS', usernameVariable: 'DOCKERHUB_USER')]) {
            sh '''
              docker login -u "$DOCKERHUB_USER" -p "$DOCKERHUB_PASS"
              docker build -t  hasanalperen/todoapp:$BUILD_NUMBER .
              docker push hasanalperen/todoapp:$BUILD_NUMBER
            '''
          }
        }
      }
    }
    stage('Kubernetes Deploy') {
      steps {
        container('kubectl') {
          withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG_FILE')]) {
            sh '''
                echo "$KUBECONFIG_B64" | base64 -d > kubeconfig.yaml
                export KUBECONFIG=$(pwd)/kubeconfig.yaml
                sed -i "s|image: __IMAGE__|image: hasanalperen/todoapp:$BUILD_NUMBER|" k8s/deployment.yaml
                kubectl apply -f k8s/.
            '''
          }
        }
      }
  }
}
}
