#!groovy

def call(body) {

  def args = [:]
  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = args
  body()
  
  node('gcloud-slave') {
    stage 'clone'
    dir('andromeda') 
    {
      //git branch: 'master', url: 'ssh://afahd@gerrit.plumgrid.com:29418/andromeda'
      //sh "git fetch ssh://afahd@gerrit.plumgrid.com:29418/andromeda refs/changes/80/26680/6 && git checkout FETCH_HEAD" 
     
    } 
    
    withEnv(["PATH=/opt/plumgrid/google-cloud-sdk/bin/:/opt/pg/scripts:$PATH"]) 
    {
      //sh 'cd andromeda/gcloud/; mkdir -p build; cd build; cmake ..; make install;'
      echo "$args.name"
      stage 'build'
      echo "Starting aurora build, project:$GERRIT_PROJECT, branch:$GERRIT_BRANCH refspec:$GERRIT_REFSPEC"
      //sh "aurora build -p corelib -b master -t $BUILD_TAG"
      def string_out = readFile('logs/build_id')
      def build_id = string_out.replace("BUILD-ID=","")
      stage 'tests'
      //sh "aurora test -p corelib -b master -t local -n 4 -i 1 -l $build_id"
    }
    
    archiveArtifacts 'logs/'
     
  }
}

