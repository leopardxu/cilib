import java.nio.charset.StandardCharsets

// Importing external jar file with the help of grapes
@Grab(group='org.yaml', module='snakeyaml', version='1.17')
import org.yaml.snakeyaml.*

// Any function which does pure groovy related work is marked as NonCP
@NonCPS
List getProjects(String dep_file)
{
    Yaml yaml = new Yaml();
    Map<String, Object> yaml_map = new HashMap<String, Object>(yaml.load(dep_file));
    List projects_list = new ArrayList(yaml_map.keySet());
    return projects_list
}

@NonCPS
String getLocation(String proj, String dep_file)
{
    Yaml yaml = new Yaml();
    Map<String, Object> yaml_map = new HashMap<String, Object>(yaml.load(dep_file));
    String git_url = yaml_map.get(proj)['location']
    // Removing special characters [ and ] 
    return git_url.replace("[","").replace("]","")
}

@NonCPS
String getBranch(String proj, String dep_file)
{
    Yaml yaml = new Yaml();
    Map<String, Object> yaml_map = new HashMap<String, Object>(yaml.load(dep_file));
    String git_branch = yaml_map.get(proj)['branch']
    return git_branch.replace("[","").replace("]","")
}

def checkDependency()
{
    return fileExists('dependencies.yaml')
}

List cloneDependencies(String repo)
{
    echo "$repo"
        // Built in readFile for groovy that read a file and returns a string
        dir ("$repo")
        {
            if (checkDependency())
            {
                sh "pwd"
                String dep_input = readFile "dependencies.yaml"
                List project_list = getProjects(dep_input)
                for(int i=0; i<project_list.size();i++)
                {
                    def project_name = project_list.get(i)
                    location = getLocation(project_name,dep_input)
                    branch = getBranch(project_name,dep_input)
                    echo "Cloning dependencies from $location "
                    sh "mkdir -p $WORKSPACE/$project_name;"
                    dir ("$WORKSPACE/$project_name")
                    {
                        // built in git function to clone a repository
                        git branch: "$branch", url: "$location"
                        if(checkDependency())
                        {
                            echo "File exists"
                        }
                        else
                        {
                            echo "Does not exist"
                        }

                    }
                }
                return project_list
            }
        }
}

def clone()
{
    sh "mkdir -p $WORKSPACE/Test2;"
    dir ("Test2")
    {
        git branch: "master", url: "https://github.com/afahd/Test2.git"
    }
    
    List projects = []
    projects.push("Test2")
    
    while(!projects.isEmpty())
    {
        echo ("$projects")
        List new_list = cloneDependencies(projects.get(0));
        projects.remove(0);
        if(!new_list.isEmpty())
        {
            projects.addAll(new_list)
            echo ("$projects")
        }
    }
    
    //List new_list = cloneDependencies("Test2")
    //def value = new_list.get(0);
    //List newer_list = cloneDependencies("$value")
    //echo ("$newer_list")
    
    
    
}

return this;
