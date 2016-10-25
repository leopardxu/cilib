#!groovy

def call(body) {

  def args = [:]
  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = args
  body()
  
  node {
    
    dir('andromeda') 
    {
      git branch: 'master', url: 'ssh://gerrit.plumgrid.com:29418/coral'
    }  
    sh 'cd andromeda/gcloud/; mkdir -p build; cd build; cmake ..; make install;'
   
    echo "$args.name"
    stage 'build'
    echo "Starting aurora build, project:$GERRIT_PROJECT, branch:$GERRIT_BRANCH refspec:$GERRIT_REFSPEC"
  }
}

