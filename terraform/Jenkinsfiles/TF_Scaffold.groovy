String region = "eu-west-2"

pipeline {
  agent{
    label 'jenkins-workers'
  } //agent

  parameters {
    choice (name: "Project",     choices: ['NIA'],                           description: "Choose a project")
    choice (name: "Environment", choices: ['build', 'vp', 'ptl', 'account'], description: "Choose environment")
    choice (name: "Component",   choices: ['base', 'nhais', 'account'],      description: "Choose component")
    choice (name: "Action",      choices: ['plan', 'apply'],                 description: "Choose Terraform action")
    string (name: "Variables",   defaultValue: "",                           description: "Terrafrom variables, format: variable1=value,variable2=value")
    string (name: "Git_Branch",  defaultValue: "develop",                    description: "Git branch")
    string (name: "Git_Repo",    defaultValue: "https://github.com/nhsconnect/integration-adaptor-nhais.git", description: "Git Repo to clone")
  }

  stages {
    stage("Clone the repository") {
      steps {
        git (branch: Git_Branch, url: Git_Repo)
      }  // steps
    } // stage Clone

    stage("Terraform Plan") {
      steps {
        terraform('plan', Project, Environment, Component, region, [], [:])
      } // steps
    } // stage Terraform Plan
 
    stage("Terraform Apply") {
      when {
        expression {
          Action == "apply"
        }
      }
      steps {
        terraform(Action, Project, Environment, Component, region, [], [:])
      } // steps
    } // stage Terraform Apply
  } // stages
} // pipeline


// int terraformScaffold(String action, String project, String environment, String component, Map<String, String> tfVariables, List<String> tfParams) {

// }


int terraform(String action, String project, String, environment, String component, String region, List<String> parameters, Map<String, String> variables, Map<String, String> backendConfig=[:]) {
    List<String> variablesList=variables.collect { key, value -> "-var ${key}=${value}" }
    String command = "terraform ${action} ${parameters.join(" ")} ${variablesList.join(" ")} -var-file=etc/global.tfvars -var-file=etc/${region}_${environment}.tfvars"
    return sh(label:"Terraform: "+action, script: command, returnStatus=true)
}